package xyz.thewind.music.provider

import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import xyz.thewind.music.model.Quality
import xyz.thewind.music.support.InMemoryKeyValueStore
import kotlin.test.assertTrue

/**
 * 访问真实音乐平台的冒烟测试，平台接口随时可能变化，默认不运行：
 * `./gradlew test -Dlive=true --tests '*LiveProviderTest*'`
 */
@Tag("live")
@SpringBootTest
class LiveProviderTest {

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun store() = InMemoryKeyValueStore()
    }

    @Autowired
    lateinit var providers: List<MusicProvider>

    private fun provider(src: String) = providers.single { it.source == src }

    @ParameterizedTest
    @ValueSource(strings = ["KG", "KW", "MG", "QQ", "WY"])
    fun `search, resolve link and lyric`(src: String) {
        val provider = provider(src)
        val songs = provider.search("周杰伦", 5, 0)
        println("[$src] search -> ${songs.map { it.songName }}")
        assertTrue(songs.isNotEmpty(), "$src search returned nothing")

        val song = songs.first()
        val resolved = provider.resolveDownloadLink(song, Quality.PQ)
        println("[$src] PQ link -> ${resolved.downloadLinkMap}")

        val lyric = provider.getLyric(song.songId!!)
        println("[$src] lyric -> ${lyric.take(80)}")
        println("[$src] detail -> ${provider.getSongDetail(song.songId!!)}")
    }
}
