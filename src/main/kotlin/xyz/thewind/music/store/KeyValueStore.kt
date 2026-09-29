package xyz.thewind.music.store

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 简单的键值存储。实现必须对存储不可用的情况容错：读返回 null，写静默失败。
 */
interface KeyValueStore {
    fun get(key: String): String?
    fun set(key: String, value: String, ttl: Duration? = null)
    fun increment(key: String, delta: Long = 1): Long?
    fun isAvailable(): Boolean
}

@Component
class RedisKeyValueStore(private val redis: StringRedisTemplate) : KeyValueStore {
    private val log = LoggerFactory.getLogger(RedisKeyValueStore::class.java)

    override fun get(key: String): String? = safely { redis.opsForValue().get(key) }

    override fun set(key: String, value: String, ttl: Duration?) {
        safely {
            if (ttl == null) redis.opsForValue().set(key, value) else redis.opsForValue().set(key, value, ttl)
        }
    }

    override fun increment(key: String, delta: Long): Long? = safely { redis.opsForValue().increment(key, delta) }

    override fun isAvailable(): Boolean =
        safely { redis.connectionFactory?.connection?.use { !it.isClosed } } ?: false

    private fun <T> safely(block: () -> T): T? =
        try {
            block()
        } catch (e: Exception) {
            log.warn("redis unavailable: {}", e.toString())
            null
        }
}
