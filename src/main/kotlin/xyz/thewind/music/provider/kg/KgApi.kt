package xyz.thewind.music.provider.kg

import xyz.thewind.music.common.Codec
import xyz.thewind.music.common.HttpUtil
import java.util.UUID

/**
 * 酷狗接口的地址拼接与签名。
 *
 * 注意：参数表刻意使用 [HashMap] 并按旧版顺序 put，这样拼出来的 URL 参数顺序与旧版完全一致；
 * 参数值不做 URL 编码（关键词里的中文直接拼进 URL）。
 */
internal object KgApi {
    private const val SEARCH_URL = "https://complexsearch.kugou.com/v2/search/song"
    private const val DOWNLOAD_URL = "https://gateway.kugou.com/v3/url"

    /** Web 端（srcappid=2919）搜索签名用的 key */
    private const val WEB_SIGN_KEY = "NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt"

    /** Android 端（appid=1005）签名用的 key */
    private const val ANDROID_SIGN_KEY = "OIlwieks28dk2k092lksi2UIkp"

    /** 下载接口 `key` 参数的盐，与 pidversion=3001 对应 */
    private const val PID_VERSION_SECRET = "57ae12eb6890223e355ccfcb74edf70d"

    private const val DOWNLOAD_URL_MARKER = "\"url\":[\""

    /** Web 端搜索地址，[page] 从 0 开始（接口从 1 开始），[clientTimeMillis] 为毫秒时间戳。 */
    fun buildSearchUrl(
        keyword: String,
        num: Int,
        page: Int,
        clientTimeMillis: Long = System.currentTimeMillis(),
        requestId: UUID = UUID.randomUUID(),
    ): String {
        val params = HashMap<String, Any?>()
        params["userid"] = "0"
        params["area_code"] = "0"
        params["srcappid"] = "2919"
        params["page"] = (page + 1).toString()
        params["token"] = ""
        params["privilegefilter"] = "0"
        params["requestid"] = requestId
        params["pagesize"] = num.toString()
        params["clienttime"] = clientTimeMillis
        params["iscorrection"] = "1"
        params["uuid"] = "0"
        params["keyword"] = keyword
        params["mid"] = "0"
        params["dfid"] = "0"
        params["clientver"] = "2000"
        params["platform"] = "WebFilter"
        params["signature"] = sign(WEB_SIGN_KEY, params)
        return HttpUtil.buildGetUrl(SEARCH_URL, params)
    }

    /**
     * Android 端取播放地址的接口。[userId]/[token] 为 VIP 账号，未配置时为 null，
     * 此时 URL 和签名里按旧版行为写入字面量 "null"。[clientTimeSeconds] 为秒级时间戳。
     */
    fun buildDownloadUrl(
        hash: String,
        userId: String?,
        token: String?,
        clientTimeSeconds: Long = System.currentTimeMillis() / 1000,
    ): String {
        val appId = "1005"
        val mid = "0"
        val params = HashMap<String, Any?>()
        params["dfid"] = "-"
        params["hash"] = hash
        params["mtype"] = "2"
        params["album_id"] = "2900867"
        params["album_audio_id"] = "album_audio_id"
        params["module"] = ""
        params["behavior"] = "play"
        params["cmd"] = "26"
        params["uuid"] = "0"
        params["clientver"] = "10479"
        params["clienttime"] = clientTimeSeconds.toString()
        params["pid"] = "2"
        params["appid"] = appId
        params["mid"] = mid
        params["version"] = "10479"
        params["token"] = token
        params["vipType"] = "6"
        params["userid"] = userId
        params["area_code"] = "1"
        params["ptype"] = "0"
        params["pidversion"] = "3001"
        params["key"] = Codec.md5(hash + PID_VERSION_SECRET + appId + mid + userId)
        params["signature"] = sign(ANDROID_SIGN_KEY, params)
        return HttpUtil.buildGetUrl(DOWNLOAD_URL, params)
    }

    /** 签名：按 key 字典序拼接 `k=v`（无分隔符），首尾各加一次 [signKey] 后取 MD5。 */
    fun sign(signKey: String, params: Map<String, Any?>): String {
        val joined = params.keys.sorted().joinToString("") { "$it=${params[it]}" }
        return Codec.md5(signKey + joined + signKey)
    }

    /** 从下载接口响应里截取第一个播放地址（去掉 JSON 转义的反斜杠），取不到返回空串。 */
    fun parseDownloadUrl(response: String): String {
        if (!(response.contains("http:") && response.contains("\""))) return ""
        return try {
            val rest = response.substring(response.indexOf(DOWNLOAD_URL_MARKER) + DOWNLOAD_URL_MARKER.length)
            rest.substring(0, rest.indexOf("\"")).replace("\\", "")
        } catch (e: IndexOutOfBoundsException) {
            ""
        }
    }

    fun lyricSearchUrl(hash: String): String = "http://krcs.kugou.com/search?ver=1&hash=$hash"

    fun lyricDownloadUrl(id: String?, accessKey: String?): String =
        "http://lyrics.kugou.com/download?&fmt=lrc&ver=1&id=$id&accesskey=$accessKey"

    fun songDetailUrl(hash: String): String =
        "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=${hash.uppercase()}&mid=12345"

    fun search(keyword: String, num: Int, page: Int): String = HttpUtil.get(buildSearchUrl(keyword, num, page))

    fun fetchDownloadUrl(hash: String, userId: String?, token: String?): String {
        val response = HttpUtil.get(buildDownloadUrl(hash, userId, token), mapOf("x-router" to "tracker.kugou.com"))
        return parseDownloadUrl(response)
    }
}
