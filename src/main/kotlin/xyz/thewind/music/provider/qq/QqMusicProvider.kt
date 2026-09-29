package xyz.thewind.music.provider.qq

import org.springframework.stereotype.Component
import xyz.thewind.music.common.Codec
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.MusicProvider
import xyz.thewind.music.provider.qq.dto.QqLoginToken
import xyz.thewind.music.provider.qq.dto.QqLyricResponse
import xyz.thewind.music.provider.qq.dto.QqSearchResponse
import xyz.thewind.music.provider.qq.dto.QqSongDetailResponse
import xyz.thewind.music.provider.qq.dto.VkeyResponse
import xyz.thewind.music.store.MusicSettings

/**
 * QQ 音乐。songId 格式为 `songMid<>mediaMid`。
 *
 * 下载链接通过 vkey 接口获取，存储中有登录 token（见 [QqTokenRefresher]）时会带上，从而可以获取 VIP 音质。
 */
@Component
class QqMusicProvider(private val settings: MusicSettings) : MusicProvider {
    override val source = MusicSource.QQ

    override fun search(keyword: String, num: Int, page: Int): List<SongBean> =
        parseSearchResult(QqApi.search(keyword, num, page))

    override fun resolveDownloadLink(song: SongBean, quality: String): SongBean {
        val request = SongBean(songSrc = MusicSource.QQ, songId = song.songId, quality = quality)
        val links = queryDownloadLinks(listOf(request)) ?: return song
        // 接口没有返回结果时写入空串，与旧版一致
        song.downloadLinkMap[quality] = links.firstOrNull() ?: ""
        return song
    }

    override fun resolveDownloadLinks(songs: List<SongBean>): List<SongBean> {
        val links = queryDownloadLinks(songs) ?: return songs
        // 响应与请求按顺序一一对应
        links.zip(songs).forEach { (link, song) ->
            song.quality?.let { song.downloadLinkMap[it] = link }
        }
        return songs
    }

    override fun searchWithDownloadLinks(keyword: String, num: Int): List<SongBean> {
        val songs = search(keyword, num, 0)
        for (song in songs) {
            // 按该歌曲的最高音质向下解析，LQ/PQ 总是解析
            val qualities = when (song.quality) {
                Quality.ZQ -> listOf(Quality.ZQ, Quality.SQ, Quality.HQ)
                Quality.SQ -> listOf(Quality.SQ, Quality.HQ)
                Quality.HQ -> listOf(Quality.HQ)
                else -> emptyList()
            } + listOf(Quality.LQ, Quality.PQ)
            qualities.forEach { resolveDownloadLink(song, it) }
        }
        return songs
    }

    override fun getLyric(songId: String): String {
        val songMid = QqApi.splitSongId(songId).getOrNull(0) ?: return ""
        return parseLyric(QqApi.lyric(songMid))
    }

    override fun getSongDetail(songId: String): SongBean? {
        val songMid = QqApi.splitSongId(songId).getOrNull(0) ?: return null
        return parseSongDetail(QqApi.songDetail(songMid))
    }

    /** 请求下载链接，返回与 [songs] 顺序对应的链接；songId/音质不合法时返回 null。 */
    private fun queryDownloadLinks(songs: List<SongBean>): List<String>? {
        val body = QqApi.vkeyRequestBody(songs, loginToken()) ?: return null
        return parseDownloadLinks(QqApi.queryVkey(body))
    }

    private fun loginToken(): QqLoginToken? = GsonUtil.fromJson<QqLoginToken>(settings.qqLoginInfo)

    // ---------------------------------------------------------------- 响应解析

    internal fun parseSearchResult(response: String): List<SongBean> {
        val resp = GsonUtil.fromJson<QqSearchResponse>(response) ?: return emptyList()
        if (resp.code != 0) return emptyList()
        return resp.data?.data?.body?.song?.list.orEmpty().map { track ->
            SongBean(
                songSrc = MusicSource.QQ,
                songId = "${track.mid}<>${track.file?.mediaMid}",
                songName = track.name,
                singersName = track.singersName,
                singersId = track.singersId,
                albumId = track.album?.mid,
                albumName = track.album?.name,
                quality = track.quality,
                picUrl = track.picUrl,
                mvUrl = track.mv?.vid,
            )
        }
    }

    internal fun parseDownloadLinks(response: String): List<String> =
        GsonUtil.fromJson<VkeyResponse>(response)?.queryvkey?.data?.midurlinfo.orEmpty().map { it.downloadLink }

    internal fun parseLyric(response: String): String =
        Codec.base64Decode(GsonUtil.fromJson<QqLyricResponse>(response)?.lyric)

    internal fun parseSongDetail(response: String): SongBean? {
        val data = GsonUtil.fromJson<QqSongDetailResponse>(response)?.songinfo?.data ?: return null
        // 接口出错时没有 track_info，旧版读取 album 时抛异常；这里返回 null
        val track = data.trackInfo ?: return null
        val album = track.album ?: return null
        // 注意：详情里的 songId 只有 songMid（没有 <>mediaMid），与旧版一致
        return SongBean(
            songSrc = MusicSource.QQ,
            songId = track.mid,
            songName = track.name,
            singersName = track.singersName,
            singersId = track.singersId,
            albumId = album.mid,
            albumName = album.name,
            picUrl = album.mid?.takeIf { it.isNotEmpty() }?.let { QqApi.albumPicUrl(it) },
            // 有多条歌词信息时取最后一条
            lrcUrl = data.info.orEmpty()
                .lastOrNull { it.type == "lyric" && !it.content.isNullOrEmpty() }
                ?.content?.first()?.value,
            mvUrl = track.mv?.vid,
        )
    }
}
