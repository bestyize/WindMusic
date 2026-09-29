package xyz.thewind.music.provider.kw

import org.springframework.stereotype.Component
import xyz.thewind.music.common.HttpUtil
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.MusicProvider
import xyz.thewind.music.store.MusicSettings

/**
 * 酷我音乐。
 *
 * 下载链接走客户端的加密接口，配置了 VIP 账号（[MusicSettings.kwUid] / [MusicSettings.kwSid]）后可解析 VIP 歌曲。
 */
@Component
class KwMusicProvider(private val settings: MusicSettings) : MusicProvider {

    override val source = MusicSource.KW

    override fun search(keyword: String, num: Int, page: Int): List<SongBean> =
        KwParser.parseSearch(HttpUtil.get(KwApi.searchUrl(keyword, num, page)))

    override fun resolveDownloadLink(song: SongBean, quality: String): SongBean {
        val requestUrl = KwApi.playUrl(song.songId, quality, settings.kwUid, settings.kwSid)
        val link = KwParser.parsePlayUrl(HttpUtil.get(requestUrl)) ?: return song
        song.downloadLinkMap[quality] = link
        return song
    }

    override fun searchWithDownloadLinks(keyword: String, num: Int): List<SongBean> =
        search(keyword, num, 0).onEach { song ->
            DOWNLOAD_QUALITIES.forEach { resolveDownloadLink(song, it) }
        }

    override fun getLyric(songId: String): String =
        KwParser.parseLyric(HttpUtil.get(KwApi.lyricUrl(songId)))

    override fun getSongDetail(songId: String): SongBean? =
        KwParser.parseSongDetail(songId, HttpUtil.get(KwApi.songInfoUrl(songId), KwApi.songInfoHeaders()))

    private companion object {
        val DOWNLOAD_QUALITIES = listOf(Quality.ZQ, Quality.SQ, Quality.HQ, Quality.PQ, Quality.LQ)
    }
}
