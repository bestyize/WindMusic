package xyz.thewind.music.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import xyz.thewind.music.store.KeyValueStore
import java.time.Duration

/** 限制同一 IP 在 [hours] 小时内最多访问该接口 [maxCount] 次。计数存在 Redis，Redis 不可用时不限制。 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessLimit(val hours: Int = 24, val maxCount: Int = 100)

@Component
class AccessLimitInterceptor(private val store: KeyValueStore) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val limit = (handler as? HandlerMethod)?.getMethodAnnotation(AccessLimit::class.java) ?: return true
        val key = "${request.remoteAddr}:${request.contextPath}:${request.servletPath}"
        val count = store.get(key)?.toIntOrNull()
        return when {
            count == null || count == -1 -> {
                store.set(key, "1", Duration.ofHours(limit.hours.toLong()))
                true
            }
            count < limit.maxCount -> {
                store.increment(key)
                true
            }
            else -> {
                response.characterEncoding = "UTF-8"
                response.contentType = "application/json; charset=utf-8"
                response.writer.println("""{"status":-1,"msg":"访问量过大，请${limit.hours}小时后重试"}""")
                false
            }
        }
    }
}
