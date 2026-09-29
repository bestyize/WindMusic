package xyz.thewind.music.web

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import xyz.thewind.music.common.Codec
import xyz.thewind.music.support.FakeMusicProvider
import xyz.thewind.music.support.InMemoryKeyValueStore
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class MusicApiTest {

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun store() = InMemoryKeyValueStore()

        @Bean
        fun fakeProvider() = FakeMusicProvider()
    }

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var store: InMemoryKeyValueStore

    @BeforeEach
    fun reset() = store.data.clear()

    @Test
    fun `serves pages`() {
        mvc.get("/").andExpect { status { isOk() } }
        mvc.get("/player?shareId=TEST_1").andExpect { forwardedUrl("/index.html") }
    }

    @Test
    fun `search accepts form data and counts daily visits`() {
        mvc.post("/api/next/search") {
            param("src", "TEST")
            param("keyword", "晴天")
            param("num", "2")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[0].songId") { value("晴天-0-1") }
            jsonPath("$[0].downloadLinkMap") { isMap() }
            jsonPath("$[0].links") { doesNotExist() }
        }
        assertEquals("1", store.data[LocalDate.now().toString()])
    }

    @Test
    fun `download single and batch`() {
        mvc.get("/api/next/download?src=TEST&songid=9&quality=SQ").andExpect {
            jsonPath("$.downloadLinkMap.SQ") { value("https://example.com/9.SQ.mp3") }
        }
        val batch = Codec.base64Encode("""[{"songId":"1","quality":"HQ"}]""")
        mvc.post("/api/next/download") {
            param("src", "TEST")
            param("batch", batch)
        }.andExpect {
            jsonPath("$[0].downloadLinkMap.HQ") { value("https://example.com/1.HQ.mp3") }
        }
    }

    @Test
    fun `player endpoint`() {
        mvc.get("/api/next/player?shareId=TEST_5").andExpect {
            jsonPath("$[0].title") { value("晴天") }
            jsonPath("$[0].author") { value("周杰伦") }
            jsonPath("$[0].url") { value("https://example.com/5.PQ.mp3") }
        }
    }

    @Test
    fun `access limit blocks after max count`() {
        repeat(5) {
            mvc.get("/api/next/searchdown?src=TEST&keyword=a&num=1").andExpect { jsonPath("$[0].songId") { exists() } }
        }
        mvc.get("/api/next/searchdown?src=TEST&keyword=a&num=1").andExpect {
            jsonPath("$.status") { value(-1) }
            jsonPath("$.msg") { value("访问量过大，请12小时后重试") }
        }
    }

    @Test
    fun `config update forces source`() {
        val params = Codec.base64Encode("songSrc=TEST;kg_uid=123;broken")
        mvc.get("/api/configupdate?params=$params").andExpect {
            jsonPath("$.code") { value(0) }
            jsonPath("$.msg") { value("更新成功") }
        }
        mvc.get("/api/getconfig").andExpect {
            jsonPath("$.obj.songSrc") { value("TEST") }
            jsonPath("$.obj.kg_uid") { value("123") }
        }
        mvc.get("/api/next/search?src=KW&keyword=x&num=1").andExpect {
            jsonPath("$[0].songSrc") { value("TEST") }
        }
    }

    @Test
    fun `config update rejects empty params`() {
        mvc.get("/api/configupdate").andExpect {
            jsonPath("$.code") { value(-1) }
            jsonPath("$.msg") { value("更新失败") }
        }
    }
}
