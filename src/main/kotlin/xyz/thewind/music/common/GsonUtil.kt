package xyz.thewind.music.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.slf4j.LoggerFactory
import java.lang.reflect.Type

/**
 * 解析/生成第三方音乐平台接口的 JSON，解析失败返回 null。
 *
 * 注意：被 Gson 反序列化的 Kotlin 类必须有无参构造（所有属性都有默认值），
 * 否则 Gson 会绕过构造器，导致非空属性为 null。
 */
object GsonUtil {
    private val log = LoggerFactory.getLogger(GsonUtil::class.java)

    val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    fun toJson(obj: Any?): String = gson.toJson(obj)

    fun <T> fromJson(json: String?, type: Type): T? {
        if (json.isNullOrEmpty()) return null
        return try {
            gson.fromJson<T>(json, type)
        } catch (e: Exception) {
            log.warn("parse json failed, type = {}, error = {}", type.typeName, e.toString())
            null
        }
    }

    fun <T> fromJson(json: String?, clazz: Class<T>): T? = fromJson<T>(json, clazz as Type)

    inline fun <reified T> fromJson(json: String?): T? = fromJson<T>(json, object : TypeToken<T>() {}.type)
}
