package xyz.thewind.music.provider.wy

import xyz.thewind.music.common.HttpUtil
import xyz.thewind.music.common.IpHelper
import xyz.thewind.music.model.Quality
import java.net.URLEncoder

/**
 * 网易云音乐的明文 `/api/` 接口：URL 拼接、请求头和 Cookie。
 *
 * 所有请求都带随机国内 `X-Real-IP` 以绕过海外地区限制。
 */
internal object WyApi {
    /** 未配置 VIP 账号（MUSIC_U 为空串）时使用的默认 Cookie（来自 MusicTools） */
    const val DEFAULT_COOKIE =
        "MUSIC_U=41d12a493b80de6fa49d16f6570d469a23f84bcf3394fedda977317d531e9c861d1b60489766205edfc3c0ec8cbcadd641049cea1c6bb9b6"

    /** 未知音质按 128k 请求 */
    const val DEFAULT_BIT_RATE = "128000"

    private val BIT_RATES = hashMapOf(
        Quality.ZQ to "1999000",
        Quality.SQ to "999000",
        Quality.HQ to "320000",
        Quality.PQ to "192000",
        Quality.LQ to "128000",
    )

    fun bitRateOf(quality: String): String = BIT_RATES[quality] ?: DEFAULT_BIT_RATE

    // ---------- URL ----------

    /** [page] 从 0 开始，offset = page * num */
    fun searchUrl(keyword: String, num: Int, page: Int): String =
        "http://music.163.com/api/cloudsearch/get/web?s=${URLEncoder.encode(keyword, Charsets.UTF_8)}" +
            "&type=1&offset=${page * num}&limit=$num&total=true"

    /** ids 是 JSON 数组形式的 `[id]`，URL 编码后为 `%5Bid%5D`；songId 为 null 时与旧版一致拼成 `[null]` */
    fun playUrlUrl(songId: String?, bitRate: String): String =
        "http://music.163.com/api/song/enhance/player/url?ids=${URLEncoder.encode("[$songId]", Charsets.UTF_8)}&br=$bitRate"

    fun lyricUrl(songId: String): String = "https://music.163.com/api/song/lyric?lv=0&kv=0&tv=0&id=$songId"

    fun songDetailUrl(songId: String): String = "https://music.163.com/api/song/detail?ids=%5b$songId%5d"

    /**
     * 获取播放链接用的 Cookie。
     *
     * 只有 MUSIC_U 为空串时才回退到 [DEFAULT_COOKIE]；未配置（null）时与旧版一致发送 `MUSIC_U=null`。
     */
    fun playCookie(musicU: String?): String = if (musicU == "") DEFAULT_COOKIE else "MUSIC_U=$musicU"

    // ---------- 请求 ----------

    fun search(keyword: String, num: Int, page: Int): String =
        HttpUtil.get(searchUrl(keyword, num, page), realIpHeader())

    fun playUrl(songId: String?, bitRate: String, musicU: String?): String {
        val headers = realIpHeader()
        headers["Cookie"] = playCookie(musicU)
        return HttpUtil.get(playUrlUrl(songId, bitRate), headers)
    }

    fun lyric(songId: String): String = HttpUtil.get(lyricUrl(songId), realIpHeader())

    fun songDetail(songId: String): String = HttpUtil.get(songDetailUrl(songId), realIpHeader())

    private fun realIpHeader(): HashMap<String, String> = hashMapOf("X-Real-IP" to IpHelper.randomChinaIp())
}
