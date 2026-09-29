package xyz.thewind.music.common

import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL

/**
 * 基于 HttpURLConnection 的极简 HTTP 工具。
 *
 * 各平台接口对请求头、重定向行为比较敏感，这里刻意保持与旧版一致：
 * 响应按行读取后直接拼接（丢弃换行符），失败时返回空串而不是抛异常。
 */
object HttpUtil {
    private val log = LoggerFactory.getLogger(HttpUtil::class.java)

    fun get(url: String?, headers: Map<String, String>? = null, timeoutMillis: Int = 4000): String {
        if (url == null || !url.startsWith("http")) return ""
        return try {
            val conn = open(url, "GET", timeoutMillis)
            headers?.forEach { (k, v) -> conn.addRequestProperty(k, v) }
            conn.readBody().also { conn.disconnect() }
        } catch (e: Exception) {
            log.warn("GET failed, url = {}, error = {}", url, e.toString())
            ""
        }
    }

    /** @param body 形如 `keyword=十年&num=100` 的表单或 JSON 字符串 */
    fun post(url: String?, body: String, headers: Map<String, String>? = null, timeoutMillis: Int = 10000): String {
        if (url == null || !url.startsWith("http")) return ""
        return try {
            val conn = open(url, "POST", timeoutMillis)
            conn.doInput = true
            conn.doOutput = true
            headers?.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            conn.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body) }
            conn.readBody().also { conn.disconnect() }
        } catch (e: Exception) {
            log.warn("POST failed, url = {}, error = {}", url, e.toString())
            ""
        }
    }

    /** 拼接 `baseUrl?k1=v1&k2=v2`，参数值不做 URL 编码。 */
    fun buildGetUrl(baseUrl: String, params: Map<String, Any?>): String =
        if (params.isEmpty()) baseUrl
        else baseUrl + params.entries.joinToString("&", prefix = "?") { "${it.key}=${it.value}" }

    // 部分平台的 URL 带未编码的中文和 {"} 等字符，URI 会拒绝，只能用宽松的 URL 构造器
    @Suppress("DEPRECATION")
    private fun open(url: String, method: String, timeoutMillis: Int): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = timeoutMillis
            readTimeout = timeoutMillis
        }

    private fun HttpURLConnection.readBody(): String =
        inputStream.bufferedReader(Charsets.UTF_8).useLines { it.joinToString("") }
}
