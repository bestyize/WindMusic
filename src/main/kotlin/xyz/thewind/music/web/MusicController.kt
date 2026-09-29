package xyz.thewind.music.web

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.thewind.music.model.PlayerBean
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.service.MusicService
import xyz.thewind.music.store.KeyValueStore
import xyz.thewind.music.store.MusicSettings
import java.time.LocalDate

/**
 * 音乐接口。参数同时支持 query string 和表单（前端用 FormData POST）。
 * 服务端配置了强制音源时（[MusicSettings.forcedSource]），搜索、下载、歌词接口忽略请求里的 `src`。
 */
@RestController
@RequestMapping("/api")
class MusicController(
    private val musicService: MusicService,
    private val settings: MusicSettings,
    private val store: KeyValueStore,
) {

    @AccessLimit(hours = 12, maxCount = 20)
    @RequestMapping("/next/search")
    fun search(keyword: String?, num: String?, page: String?, src: String?): List<SongBean> {
        // 按天统计搜索量，key 形如 2026-09-29
        store.increment(LocalDate.now().toString())
        return musicService.search(sourceOf(src), keyword.orEmpty(), num.toIntOr(10), page.toIntOr(0))
    }

    /** 单曲：传 songid + quality；批量：传 batch（Base64 编码的 SongBean 数组），返回数组。 */
    @AccessLimit(hours = 12, maxCount = 20)
    @RequestMapping("/next/download")
    fun download(songid: String?, quality: String?, src: String?, batch: String?): Any =
        if (batch == null) musicService.resolveDownloadLink(sourceOf(src), songid, quality.orEmpty())
        else musicService.resolveDownloadLinks(sourceOf(src), batch)

    @AccessLimit(hours = 12, maxCount = 5)
    @RequestMapping("/next/searchdown")
    fun searchWithDownloadLinks(keyword: String?, num: String?, src: String?): List<SongBean> =
        musicService.searchWithDownloadLinks(sourceOf(src), keyword.orEmpty(), num.toIntOr(10))

    @AccessLimit(hours = 12, maxCount = 20)
    @RequestMapping("/getlyric")
    fun lyric(songid: String?, src: String?): String = musicService.getLyric(sourceOf(src), songid.orEmpty())

    @AccessLimit(hours = 12, maxCount = 20)
    @RequestMapping("/next/player")
    fun player(shareId: String?): List<PlayerBean> = musicService.getPlayerInfo(shareId)

    @AccessLimit(hours = 12, maxCount = 50)
    @RequestMapping("/songdetail")
    fun songDetail(src: String?, songid: String?): SongBean? = musicService.getSongDetail(src, songid.orEmpty())

    private fun sourceOf(requested: String?): String? = settings.forcedSource ?: requested

    private fun String?.toIntOr(default: Int): Int = this?.trim()?.toIntOrNull() ?: default
}
