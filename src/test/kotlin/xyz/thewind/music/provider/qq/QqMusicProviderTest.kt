package xyz.thewind.music.provider.qq

import org.junit.jupiter.api.Test
import xyz.thewind.music.store.MusicSettings
import xyz.thewind.music.support.InMemoryKeyValueStore
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** 解析结果的期望值由旧版 Java 代码对相同 JSON 运行得到。 */
class QqMusicProviderTest {
    private val provider = QqMusicProvider(MusicSettings(InMemoryKeyValueStore()))

    @Test
    fun `parse search result`() {
        val json = """{"code":0,"data":{"code":0,"data":{"body":{"song":{"list":[""" +
            """{"album":{"id":1,"mid":"alb1","name":"叶惠美"},"file":{"media_mid":"med1","size_128mp3":100,"size_320mp3":200,"size_flac":300,"size_hires":400},"mid":"mid1","interval":269,"mv":{"vid":"v1"},"name":"晴天","singer":[{"id":4558,"mid":"s1","name":"周杰伦"},{"id":5,"mid":"s2","name":"B"}]},""" +
            """{"album":{"mid":"alb2","name":"A2"},"file":{"media_mid":"med2","size_flac":300,"size_hires":0},"mid":"mid2","mv":{"vid":""},"name":"N2","singer":[]},""" +
            """{"album":{"mid":"","name":""},"file":{"media_mid":"med3"},"mid":"mid3","mv":{"vid":""},"name":"N3","singer":[{"mid":"s3","name":"C"}]}""" +
            """]}}}}}"""
        val actual = provider.parseSearchResult(json).map {
            listOf(
                it.songId, it.songName, it.singersName.toString(), it.singersId.toString(),
                it.albumId, it.albumName, it.quality, it.picUrl, it.mvUrl,
            ).joinToString("|")
        }
        assertEquals(
            listOf(
                "mid1<>med1|晴天|[周杰伦, B]|[s1, s2]|alb1|叶惠美|ZQ|https://y.gtimg.cn/music/photo_new/T002R800x800M000alb1.jpg|v1",
                "mid2<>med2|N2|[]|[]|alb2|A2|SQ|https://y.gtimg.cn/music/photo_new/T002R800x800M000alb2.jpg|",
                "mid3<>med3|N3|[C]|[s3]|||HQ|https://y.gtimg.cn/music/photo_new/T002R800x800M000.jpg|",
            ),
            actual,
        )
        assertTrue(provider.parseSearchResult("""{"code":1}""").isEmpty())
        assertTrue(provider.parseSearchResult("").isEmpty())
    }

    @Test
    fun `parse download links`() {
        val json = """{"code":0,"queryvkey":{"code":0,"data":{"expiration":80400,"midurlinfo":[{"filename":"F000a.flac","purl":"F000a.flac?guid=810BA5&vkey=ABC&uin=115","songmid":"a","qmdlfromtag":"66"},{"filename":"C400b.m4a","purl":"","songmid":"b","qmdlfromtag":""}]}}}"""
        assertEquals(
            listOf(
                "https://isure.stream.qqmusic.qq.com/F000a.flac?guid=810BA5&vkey=ABC&uin=115&fromtag=66",
                "https://isure.stream.qqmusic.qq.com/&fromtag=",
            ),
            provider.parseDownloadLinks(json),
        )
        assertTrue(provider.parseDownloadLinks("").isEmpty())
    }

    @Test
    fun `parse song detail`() {
        val json = """{"code":0,"songinfo":{"code":0,"data":{"track_info":{"album":{"mid":"alb1","name":"叶惠美"},"mid":"mid1","mv":{"vid":"v1"},"name":"晴天","singer":[{"mid":"s1","name":"周杰伦"}]},"info":[{"title":"发行时间","type":"pub_time","content":[{"value":"2003-07-31"}]},{"title":"歌词","type":"lyric","content":[{"value":"lyric-v1","mid":""}]},{"title":"歌词","type":"lyric","content":[]}]}}}"""
        val song = assertNotNull(provider.parseSongDetail(json))
        assertEquals(
            "mid1|晴天|[周杰伦]|[s1]|alb1|叶惠美|https://y.gtimg.cn/music/photo_new/T002R800x800M000alb1.jpg|lyric-v1|v1",
            listOf(
                song.songId, song.songName, song.singersName.toString(), song.singersId.toString(),
                song.albumId, song.albumName, song.picUrl, song.lrcUrl, song.mvUrl,
            ).joinToString("|"),
        )
        assertEquals("QQ", song.songSrc)
    }

    @Test
    fun `parse lyric`() {
        val json = """{"retcode":0,"code":0,"lyric":"WzAwOjAwLjAwXeaZtOWkqSAtIOWRqOadsOS8pgpbMDA6MDEuMDBd6K+N77ya5ZGo5p2w5Lym"}"""
        assertEquals("[00:00.00]晴天 - 周杰伦\n[00:01.00]词：周杰伦", provider.parseLyric(json))
        assertEquals("", provider.parseLyric(""))
    }
}
