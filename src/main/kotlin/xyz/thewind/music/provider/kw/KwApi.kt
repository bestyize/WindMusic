package xyz.thewind.music.provider.kw

import xyz.thewind.music.model.Quality
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 酷我各接口的请求地址与请求头。只负责拼装，不发请求。
 */
internal object KwApi {

    /** 取播放地址的接口，`q` 参数是 [KwCrypto] 加密后的查询串 */
    private const val PLAY_URL_BASE = "http://nmobi.kuwo.cn/mobi.s?f=kuwo&q="

    /** 音质 -> 接口的 br 参数；未知音质会拼成 `br=null`，与旧版一致 */
    private val QUALITY_BR = mapOf(
        Quality.ZQ to "4000kflac",
        Quality.SQ to "2000kflac",
        Quality.HQ to "320kmp3",
        Quality.PQ to "192kmp3",
        Quality.LQ to "128kmp3",
    )

    fun searchUrl(keyword: String, num: Int, page: Int): String =
        "http://search.kuwo.cn/r.s?user=0&all=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
            "&pn=" + page + "&rn=" + num +
            "&uid=0&ver=kwplayer_ar_10.1.5.0&vipver=1&newver=3&correct=1&ft=music&cluster=0&strategy=2012" +
            "&encoding=utf8&rformat=json&vermerge=1&mobi=1&searchapi=5"

    /**
     * 获取播放地址的完整请求链接，支持 Hi-Res 音质。
     * [uid]/[sid] 为酷我 VIP 账号的 loginUid/loginSid，未配置时按旧版原样拼成 `null`。
     */
    fun playUrl(
        songId: String?,
        quality: String,
        uid: String?,
        sid: String?,
        timestampSeconds: Long = System.currentTimeMillis() / 1000,
    ): String = PLAY_URL_BASE + KwCrypto.encrypt(playUrlQuery(songId, quality, uid, sid, timestampSeconds))

    /** 加密前的明文查询串，参数顺序不可改动 */
    fun playUrlQuery(songId: String?, quality: String, uid: String?, sid: String?, timestampSeconds: Long): String =
        "prod=kwplayer_ar_10.3.6.3" +
            "&corp=kuwo" +
            "&newver=3" +
            "&vipver=10.3.6.3" +
            "&source=kwplayer_ar_10.3.6.3_40.apk" +
            "&loginUid=" + uid +
            "&notrace=1" +
            "&type=convert_url_with_sign" +
            "&br=" + QUALITY_BR[quality] +
            "&format=flac|mp3|aac" +
            "&rid=" + songId +
            "&priority=bitrate" +
            "&loginSid=" + sid +
            "&mode=audition" +
            "&timestamp=" + timestampSeconds +
            "&isstar=false"

    /** 歌词（与歌曲信息同一接口），例如 http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=80488731 */
    fun lyricUrl(songId: String): String = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=$songId"

    fun songInfoUrl(songId: String): String = "http://www.kuwo.cn/api/www/music/musicInfo?mid=$songId"

    /** 网页版接口要求 Cookie 中的 kw_token 与 csrf 请求头一致，值本身可以是任意串 */
    fun songInfoHeaders(): Map<String, String> = hashMapOf(
        "Cookie" to "kw_token=62COOIBCS34",
        "csrf" to "62COOIBCS34",
        "Keep-Alive" to "close",
    )
}
