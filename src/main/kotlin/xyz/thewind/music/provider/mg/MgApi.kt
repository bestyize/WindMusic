package xyz.thewind.music.provider.mg

import xyz.thewind.music.common.Codec
import xyz.thewind.music.common.HttpUtil
import xyz.thewind.music.model.USER_AGENT_ANDROID
import java.net.URLEncoder
import java.util.Random
import java.util.UUID

/**
 * 咪咕接口的请求构造与签名，只负责发请求并返回原始响应，不做解析。
 *
 * 搜索走咪咕 App（Android 7.21.0）的 jadeite 搜索接口，需要一组模拟设备的请求头和 MD5 签名；
 * 资源信息、歌词接口不需要签名。
 */
internal object MgApi {
    private const val SEARCH_URL = "https://jadeite.migu.cn/music_search/v3/search/searchAll"

    // 备选：https://app.c.nf.migu.cn/MIGUM3.0/v2.0/content/querySongBySongId.do?songId=1136379763
    private const val RESOURCE_URL =
        "https://c.musicapp.migu.cn/MIGUM3.0/v1.0/content/resourceinfo.do?ua=Android_migu&version=5.0&resourceType=2&copyrightId="

    private const val LYRIC_URL = "https://m.music.migu.cn/migu/remoting/cms_detail_tag?cpid="

    /** 搜索类型开关：只搜歌曲和标签歌曲 */
    private const val SEARCH_SWITCH =
        """{"song":1,"album":0,"singer":0,"tagSong":1,"mvSong":0,"bestShow":0,"songlist":0,"lyricSong":0}"""

    /** 咪咕签名的 MD5 */
    private const val SIGNATURE_MD5 = "6cdc72a439cef99a3418d2a78aa28c73"

    /** appId(yyapp2) 与固定密钥拼接而成，参与签名 */
    private const val SIGN_APP_KEY = "yyapp2d16148780a1dcc7408e06336b98cfd50"

    /** 注意字符表里 0 出现了两次（沿用旧版，保证同一种子生成的设备号不变） */
    private const val DEVICE_ID_CHARS = "01234567890ABCDEF"

    private val CLIENT_IP = Codec.base64Encode("114.88.161.51")

    /** 搜索歌曲，返回原始 JSON（已 trim）。[page] 原样作为 pageNo 传给咪咕。 */
    fun search(keyword: String, num: Int, page: Int): String {
        val text = keyword.trim()
        val msisdn = Codec.base64Encode(randomPhoneNumber())
        return HttpUtil.get(searchUrl(text, num, page), searchHeaders(text, msisdn)).trim()
    }

    /** 歌曲资源信息（各音质下载地址、歌词地址、封面等），[songId] 即 copyrightId。 */
    fun resourceInfo(songId: String): String {
        val headers = HashMap<String, String>()
        headers["User-Agent"] = USER_AGENT_ANDROID
        // Host 与实际域名不一致，HttpURLConnection 默认会忽略这个受限请求头，沿用旧版照样传
        headers["Host"] = "m.music.migu.cn"
        return HttpUtil.get(RESOURCE_URL + songId, headers)
    }

    fun lyric(songId: String): String {
        val headers = HashMap<String, String>()
        headers["Referer"] = "https://m.music.migu.cn/v3/music/song/$songId"
        return HttpUtil.get(LYRIC_URL + songId, headers)
    }

    /**
     * 搜索地址。参数顺序由 HashMap 的遍历顺序决定，与旧版一致；
     * 末尾保留多余的 `&`（旧版就是这样拼的），所以不用 [HttpUtil.buildGetUrl]。
     */
    fun searchUrl(keyword: String, num: Int, page: Int, sid: String = randomSid()): String {
        val params = HashMap<String, String>()
        params["feature"] = "1011000000"
        params["pageNo"] = page.toString()
        params["pageSize"] = num.toString()
        params["sort"] = "0"
        params["text"] = URLEncoder.encode(keyword, Charsets.UTF_8)
        params["searchSwitch"] = URLEncoder.encode(SEARCH_SWITCH, Charsets.UTF_8)
        params["sid"] = sid
        params["isCopyright"] = "1"
        params["isCorrect"] = "1"
        return "$SEARCH_URL?" + params.entries.joinToString("") { "${it.key}=${it.value}&" }
    }

    /** 模拟 Android 客户端的搜索请求头，[msisdn] 为 Base64 编码后的手机号。 */
    fun searchHeaders(
        keyword: String,
        msisdn: String,
        deviceId: String = randomDeviceId(),
        timestamp: String = System.currentTimeMillis().toString(),
    ): Map<String, String> {
        val headers = HashMap<String, String>()
        headers["deviceId"] = deviceId
        headers["appId"] = "yyapp2"
        headers["android_id"] = "afad815163f1d394"
        headers["ua"] = "Android_migu"
        headers["version"] = "7.21.0"
        headers["uiVersion"] = "A_music_3.12.4"
        headers["ip"] = CLIENT_IP
        headers["sign"] = sign(keyword, deviceId, msisdn, timestamp)
        headers["timestamp"] = timestamp
        headers["msisdn"] = msisdn
        headers["os"] = "android 12"
        headers["mgm-Network-type"] = "04"
        headers["mode"] = "android"
        return headers
    }

    /** sign = md5(关键词 + 签名MD5 + appId密钥 + deviceId + msisdn + 时间戳) */
    fun sign(keyword: String, deviceId: String, msisdn: String, timestamp: String): String =
        Codec.md5(keyword + SIGNATURE_MD5 + SIGN_APP_KEY + deviceId + msisdn + timestamp)

    /** 两个去掉横线的 UUID 拼接，共 64 位十六进制 */
    fun randomSid(): String =
        UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "")

    /** 188 开头的 11 位随机手机号。必须用 java.util.Random，同一种子才能与旧版结果一致。 */
    fun randomPhoneNumber(seed: Long = System.currentTimeMillis()): String {
        val random = Random(seed)
        return buildString {
            append("188")
            repeat(8) { append(random.nextInt(10)) }
        }
    }

    fun randomDeviceId(length: Int = 32, seed: Long = System.currentTimeMillis()): String {
        val random = Random(seed)
        return buildString {
            repeat(length) { append(DEVICE_ID_CHARS[random.nextInt(DEVICE_ID_CHARS.length)]) }
        }
    }
}
