package xyz.thewind.music.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import xyz.thewind.music.common.Codec
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.common.javaSplit
import xyz.thewind.music.model.PlayerBean
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.MusicProvider

/**
 * 按音源代号把请求分发给对应平台的 [MusicProvider]。未知音源返回空结果。
 */
@Service
class MusicService(providers: List<MusicProvider>) {
    private val log = LoggerFactory.getLogger(MusicService::class.java)

    private val providers: Map<String, MusicProvider> = providers.associateBy { it.source }

    fun search(src: String?, keyword: String, num: Int, page: Int): List<SongBean> =
        providers[src]?.search(keyword, num, page).orEmpty()

    fun resolveDownloadLink(src: String?, songId: String?, quality: String): SongBean {
        val song = SongBean(songSrc = src, songId = songId)
        val provider = providers[src] ?: return song
        if (songId == null) return song
        return provider.resolveDownloadLink(song, quality)
    }

    /** @param batch Base64 编码的 SongBean JSON 数组，每项至少包含 songId 和 quality */
    fun resolveDownloadLinks(src: String?, batch: String): List<SongBean> {
        val provider = providers[src] ?: return emptyList()
        return try {
            val songs = GsonUtil.fromJson<List<SongBean>>(Codec.base64Decode(batch)) ?: return emptyList()
            provider.resolveDownloadLinks(songs)
        } catch (e: Exception) {
            log.warn("batch download failed, src = {}, error = {}", src, e.toString())
            emptyList()
        }
    }

    fun searchWithDownloadLinks(src: String?, keyword: String, num: Int): List<SongBean> =
        providers[src]?.searchWithDownloadLinks(keyword, num).orEmpty()

    fun getLyric(src: String?, songId: String): String = providers[src]?.getLyric(songId).orEmpty()

    fun getSongDetail(src: String?, songId: String): SongBean? = providers[src]?.getSongDetail(songId)

    /**
     * 分享播放页的数据。[shareId] 格式为 `音源_歌曲ID`，例如 `KW_80488731`。
     * 歌名歌手优先取歌曲详情，取不到时从歌词头部解析。
     */
    fun getPlayerInfo(shareId: String?): List<PlayerBean> {
        val parts = shareId?.javaSplit("_") ?: return emptyList()
        if (parts.size != 2) return emptyList()
        val (src, songId) = parts

        val song = resolveDownloadLink(src, songId, Quality.PQ)
        val url = song.downloadLinkMap[Quality.PQ] ?: song.downloadLinkMap[Quality.LQ] ?: ""
        var pic = song.picUrl?.takeIf { it.length >= 10 } ?: DEFAULT_COVER

        val lyric = getLyric(src, songId)
        var title = LyricHeader.songName(src, lyric)
        var author = LyricHeader.singerName(src, lyric)

        getSongDetail(src, songId)?.let { detail ->
            title = detail.songName ?: title
            val singers = detail.singersName.orEmpty()
            when {
                singers.size == 1 -> author = singers[0]
                singers.size > 1 -> author = "${singers[0]}&${singers[1]}"
            }
            detail.picUrl?.let { pic = it }
        }
        return listOf(PlayerBean(title = title, author = author, pic = pic, url = url, lyc = lyric))
    }

    private companion object {
        const val DEFAULT_COVER = "img/wind-icon-256.png"
    }
}
