package xyz.thewind.music.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import xyz.thewind.music.support.FakeMusicProvider
import xyz.thewind.music.support.InMemoryKeyValueStore
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** 在真实 Tomcat 上验证：经本机 nginx 转发时，限流按 X-Forwarded-For 里的客户端 IP 计数。 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReverseProxyTest {

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun store() = InMemoryKeyValueStore()

        @Bean
        fun fakeProvider() = FakeMusicProvider()
    }

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var store: InMemoryKeyValueStore

    private val client = HttpClient.newHttpClient()

    private fun get(path: String, forwardedFor: String?): HttpResponse<String> {
        val request = HttpRequest.newBuilder(URI("http://127.0.0.1:$port$path"))
            .apply { forwardedFor?.let { header("X-Forwarded-For", it) } }
            .build()
        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    @Test
    fun `access limit counts the forwarded client ip`() {
        store.data.clear()
        assertEquals(200, get("/api/getlyric?src=TEST&songid=1", "203.0.113.7").statusCode())
        get("/api/getlyric?src=TEST&songid=1", "198.51.100.9")
        get("/api/getlyric?src=TEST&songid=1", null)

        val limitKeys = store.data.keys.filter { it.endsWith(":/api/getlyric") }
        assertEquals(
            setOf("203.0.113.7::/api/getlyric", "198.51.100.9::/api/getlyric", "127.0.0.1::/api/getlyric"),
            limitKeys.toSet(),
        )
        assertTrue(store.data.values.all { it == "1" })
    }
}
