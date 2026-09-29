package xyz.thewind.music.provider.mg

import org.springframework.stereotype.Component
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.MusicProvider
import xyz.thewind.music.provider.mg.dto.MgLyricResponse
import xyz.thewind.music.provider.mg.dto.MgResourceResponse
import xyz.thewind.music.provider.mg.dto.MgSearchResponse

/**
 * 咪咕音乐。songId 为咪咕的 copyrightId。
 *
 * 搜索结果只带元信息和推断出的最高音质；下载链接、歌曲详情都来自 resourceinfo 接口，
 * 一次请求就能拿到全部音质的链接。
 */
@Component
class MgMusicProvider : MusicProvider {
    override val source = MusicSource.MG

    override fun search(keyword: String, num: Int, page: Int): List<SongBean> =
        parseSearchResult(MgApi.search(keyword, num, page))

    /** 请求的音质不存在时原样返回 [song]；存在时把该歌曲全部音质的链接都写入。 */
    override fun resolveDownloadLink(song: SongBean, quality: String): SongBean {
        val songId = song.songId ?: return song
        val links = fetchResource(songId).downloadLinkMap
        if (links[quality] == null) return song
        song.downloadLinkMap = links
        return song
    }

    /** 只按 HQ 判断：没有 HQ 链接的歌曲不会写入任何链接。 */
    override fun searchWithDownloadLinks(keyword: String, num: Int): List<SongBean> =
        search(keyword, num, 0).onEach { resolveDownloadLink(it, Quality.HQ) }

    override fun getLyric(songId: String): String =
        GsonUtil.fromJson(MgApi.lyric(songId), MgLyricResponse::class.java)?.data?.lyricLrc.orEmpty()

    /** 失败时返回只有 songSrc/songId 的 SongBean，而不是 null。 */
    override fun getSongDetail(songId: String): SongBean = fetchResource(songId)

    private fun fetchResource(songId: String): SongBean = parseResource(songId, MgApi.resourceInfo(songId))

    internal fun parseSearchResult(json: String): List<SongBean> {
        val response = GsonUtil.fromJson(json, MgSearchResponse::class.java) ?: return emptyList()
        return response.result?.resultList.orEmpty()
            .mapNotNull { versions -> versions?.firstOrNull() }
            .map { detail ->
                SongBean(
                    songSrc = MusicSource.MG,
                    songId = detail.songId,
                    songName = detail.songName,
                    singersId = detail.singersId,
                    singersName = detail.singersName,
                    albumId = detail.albumId,
                    albumName = detail.albumName,
                    quality = detail.quality,
                    picUrl = detail.picUrl,
                    lrcUrl = detail.lyricUrl,
                    // 前端拿 mvUrl 字段回传 mvId
                    mvUrl = detail.mvId,
                )
            }
    }

    /** 解析 resourceinfo 响应；解析失败时返回只有 songSrc/songId 的 SongBean。 */
    internal fun parseResource(songId: String, json: String): SongBean {
        val song = SongBean(songSrc = MusicSource.MG, songId = songId)
        val detail = GsonUtil.fromJson(json, MgResourceResponse::class.java)?.resource?.firstOrNull() ?: return song

        song.lrcUrl = detail.lrcUrl
        val links = song.downloadLinkMap
        // 旧版直接 put 可能为 null 的 url（等价于"该音质没有链接"），这里用 remove 表达同样的语义
        fun putLink(quality: String?, url: String?) {
            if (quality == null) return
            if (url == null) links.remove(quality) else links[quality] = url
        }
        detail.rateFormats.orEmpty().forEach { putLink(it?.formatType, it?.url) }
        detail.eqFormat?.let { putLink(Quality.EQ, it.url) }

        song.songName = detail.songName
        song.albumName = detail.albumName
        song.singersName = listOfNotNull(detail.singerName)
        song.singersId = listOfNotNull(detail.singerId)
        detail.albumImgs.orEmpty().firstOrNull()?.let { song.picUrl = it.img }
        return song
    }
}
