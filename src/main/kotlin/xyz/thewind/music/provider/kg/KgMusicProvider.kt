package xyz.thewind.music.provider.kg

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import xyz.thewind.music.common.Codec
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.common.HttpUtil
import xyz.thewind.music.common.javaSplit
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.MusicProvider
import xyz.thewind.music.provider.kg.dto.LyricDownloadResponse
import xyz.thewind.music.provider.kg.dto.LyricSearchResponse
import xyz.thewind.music.provider.kg.dto.PlayDataResponse
import xyz.thewind.music.provider.kg.dto.SearchResponse
import xyz.thewind.music.store.MusicSettings

/**
 * 酷狗音乐。
 *
 * songId 格式为 `PQ-HQ-SQ-ZQ` 四个音质的文件 hash，详见 [xyz.thewind.music.provider.kg.dto.SearchSong.songId]。
 * 下载链接用 [MusicSettings.kgUid]/[MusicSettings.kgToken] 配置的 VIP 账号请求。
 */
@Component
class KgMusicProvider(private val settings: MusicSettings) : MusicProvider {
    override val source = MusicSource.KG

    override fun search(keyword: String, num: Int, page: Int): List<SongBean> =
        parseSearchResult(KgApi.search(keyword, num, page))

    override fun resolveDownloadLink(song: SongBean, quality: String): SongBean {
        val songId = song.songId
        if (songId != null && songId.length >= 32) {
            song.downloadLinkMap[quality] = fetchDownloadUrl(selectHash(songId, quality))
        }
        return song
    }

    override fun searchWithDownloadLinks(keyword: String, num: Int): List<SongBean> {
        val songs = search(keyword, num, 0)
        for (song in songs) {
            val hashes = song.songId?.javaSplit("-") ?: continue
            // hash 与音质按位置一一对应；末尾缺失的 hash 被 split 丢弃，对应音质也就不请求
            hashes.zip(QUALITY_ORDER).forEach { (hash, quality) ->
                song.downloadLinkMap[quality] = fetchDownloadUrl(hash)
            }
        }
        return songs
    }

    /**
     * 先按 hash 搜索歌词候选，再用第一个候选的 id + accesskey 下载 Base64 编码的 LRC。
     *
     * 也可以先请求 https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=...&mid=12345 获取 album_id，
     * 再带上 album_id 请求同一接口获取歌词。
     */
    override fun getLyric(songId: String): String {
        if (songId.length < 32) return ""
        val hash = firstHash(songId)
        return try {
            val candidate = GsonUtil.fromJson<LyricSearchResponse>(HttpUtil.get(KgApi.lyricSearchUrl(hash)))
                ?.candidates?.firstOrNull() ?: return ""
            val lyric = GsonUtil.fromJson<LyricDownloadResponse>(
                HttpUtil.get(KgApi.lyricDownloadUrl(candidate.id, candidate.accesskey)),
            ) ?: return ""
            Codec.base64Decode(lyric.content)
        } catch (e: Exception) {
            log.warn("getLyric failed, songId = {}, error = {}", songId, e.toString())
            ""
        }
    }

    override fun getSongDetail(songId: String): SongBean? {
        if (songId.length < 20) return null
        return try {
            parseSongDetail(songId, HttpUtil.get(KgApi.songDetailUrl(firstHash(songId))))
        } catch (e: Exception) {
            log.warn("getSongDetail failed, songId = {}, error = {}", songId, e.toString())
            null
        }
    }

    private fun fetchDownloadUrl(hash: String): String =
        KgApi.fetchDownloadUrl(hash, settings.kgUid, settings.kgToken)

    companion object {
        private val log = LoggerFactory.getLogger(KgMusicProvider::class.java)

        /** songId 中各段 hash 对应的音质 */
        private val QUALITY_ORDER = listOf(Quality.PQ, Quality.HQ, Quality.SQ, Quality.ZQ)

        /**
         * 按音质从 songId 中取 hash，取不到对应位置时退回第一段（PQ）。
         *
         * 注意：与旧版保持一致，SQ 只在 songId 恰好 3 段时生效；搜索结果带 ZQ hash（4 段）时请求 SQ 实际取的是 PQ hash。
         */
        internal fun selectHash(songId: String, quality: String): String {
            val hashes = songId.javaSplit("-")
            var hash = hashes[0]
            if (quality == Quality.ZQ && hashes.size == 4) {
                hash = hashes[3]
            }
            if (quality == Quality.SQ && hashes.size == 3) {
                hash = hashes[2]
            } else if (quality == Quality.HQ && hashes.size >= 2) {
                hash = hashes[1]
            }
            return hash
        }

        private fun firstHash(songId: String): String =
            if (songId.contains("-")) songId.substring(0, songId.indexOf("-")) else songId

        internal fun parseSearchResult(response: String): List<SongBean> {
            if (response.isEmpty()) {
                log.warn("parseSearchResult, empty response")
                return emptyList()
            }
            val songs = GsonUtil.fromJson<SearchResponse>(response)?.data?.lists.orEmpty().map { detail ->
                SongBean(
                    songSrc = MusicSource.KG,
                    songId = detail.songId,
                    songName = detail.songName,
                    singersId = detail.singersId,
                    singersName = detail.singersName,
                    albumId = detail.albumId,
                    albumName = detail.albumName,
                    quality = detail.quality,
                    mvUrl = detail.mvId,
                )
            }
            if (songs.isEmpty()) {
                log.warn("parseSearchResult, error resp = {}", response)
            }
            return songs
        }

        /** 解析 play/getdata 的响应，[songId] 原样写回（保留完整的多段 hash）。 */
        internal fun parseSongDetail(songId: String, response: String): SongBean? {
            val data = GsonUtil.fromJson<PlayDataResponse>(response)?.data ?: return null
            val authors = data.authors ?: return null
            return SongBean(
                songSrc = MusicSource.KG,
                songId = songId,
                songName = data.songName,
                picUrl = data.picUrl,
                singersId = authors.map { it.singerId.orEmpty() },
                singersName = authors.map { it.singerName.orEmpty() },
                albumName = data.albumName,
                albumId = data.albumId,
            )
        }
    }
}
