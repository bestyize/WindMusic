package xyz.thewind.music.provider.wy

import org.springframework.stereotype.Component
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.MusicProvider
import xyz.thewind.music.store.MusicSettings

/**
 * 网易云音乐。
 *
 * songId 为网易云歌曲 id。下载链接用 [MusicSettings.wyMusicU] 配置的 VIP 账号请求，
 * 规则见 [WyApi.playCookie]。
 */
@Component
class WyMusicProvider(private val settings: MusicSettings) : MusicProvider {
    override val source = MusicSource.WY

    override fun search(keyword: String, num: Int, page: Int): List<SongBean> =
        WyParser.parseSearchResult(WyApi.search(keyword, num, page))

    override fun resolveDownloadLink(song: SongBean, quality: String): SongBean {
        val response = WyApi.playUrl(song.songId, WyApi.bitRateOf(quality), settings.wyMusicU)
        val url = WyParser.parsePlayUrl(response)
        // 旧版取不到链接时直接 put(quality, null)，Gson 输出时会省略该项；这里等价地移除
        if (url != null) song.downloadLinkMap[quality] = url else song.downloadLinkMap.remove(quality)
        return song
    }

    override fun searchWithDownloadLinks(keyword: String, num: Int): List<SongBean> {
        val songs = search(keyword, num, 0)
        for (song in songs) {
            ALL_QUALITIES.forEach { resolveDownloadLink(song, it) }
        }
        return songs
    }

    override fun getLyric(songId: String): String = WyParser.parseLyric(WyApi.lyric(songId))

    override fun getSongDetail(songId: String): SongBean? =
        WyParser.parseSongDetail(songId, WyApi.songDetail(songId))

    private companion object {
        val ALL_QUALITIES = listOf(Quality.ZQ, Quality.SQ, Quality.HQ, Quality.PQ, Quality.LQ)
    }
}
