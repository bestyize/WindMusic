package xyz.thewind.music.provider.wy

import org.junit.jupiter.api.Test
import xyz.thewind.music.model.SongBean
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 期望值由旧版 Java 代码（WyMusicModel 的解析逻辑）对同样的输入运行得出。 */
class WyParserTest {

    private fun SongBean.fmt(): String =
        "$songSrc|$songId|$songName|$singersId|$singersName|$albumId|$albumName|$picUrl|$quality|$mvUrl"

    @Test
    fun `parse search result`() {
        val json = """{"result":{"songs":[{"id":65766,"name":"富士山下","ar":[{"id":2116,"name":"陈奕迅"}],"al":{"id":6436,"name":"What's Going On…?","picUrl":"http://p1.music.126.net/a.jpg","publishTime":1135008000000},"mv":5327432,"hr":{"br":1999000,"size":55000000},"sq":{"br":999000,"size":30000000},"privilege":{"maxbr":999000}},{"id":287063,"name":"十年","ar":[{"id":2116,"name":"陈奕迅"},{"id":1234,"name":"合唱"}],"al":{"id":28523,"name":"黑白灰","picUrl":"https://p2.music.126.net/b.jpg"},"mv":0,"hr":null,"privilege":{"maxbr":999000}},{"id":5257138,"name":"低音质","ar":[],"al":{"id":1,"name":"专辑"},"privilege":{"maxbr":320000}},{"id":111,"name":"缺少privilege","ar":[{"id":1,"name":"x"}],"al":{"id":2,"name":"y"}},{"id":222,"name":"不会被解析","ar":[{"id":1,"name":"x"}],"al":{"id":2,"name":"y"},"privilege":{"maxbr":999000}}],"songCount":5},"code":200}"""
        val songs = WyParser.parseSearchResult(json)
        assertEquals(
            listOf(
                "WY|65766|富士山下|[2116]|[陈奕迅]|6436|What's Going On…?|http://p1.music.126.net/a.jpg|ZQ|5327432",
                "WY|287063|十年|[2116, 1234]|[陈奕迅, 合唱]|28523|黑白灰|https://p2.music.126.net/b.jpg|SQ|0",
                "WY|5257138|低音质|[]|[]|1|专辑|null|HQ|0",
            ),
            songs.map { it.fmt() },
        )
    }

    @Test
    fun `parse search result of invalid response`() {
        assertTrue(WyParser.parseSearchResult("").isEmpty())
        assertTrue(WyParser.parseSearchResult("<html>").isEmpty())
        assertTrue(WyParser.parseSearchResult("""{"result":{}}""").isEmpty())
    }

    @Test
    fun `parse song detail`() {
        val json = """{"songs":[{"name":"十年","id":287063,"duration":205000,"artists":[{"id":2116,"name":"陈奕迅","picUrl":null},{"id":1234,"name":"合唱"}],"album":{"id":28523,"name":"黑白灰","picUrl":"http://p1.music.126.net/c.jpg"}}],"code":200}"""
        assertEquals(
            "WY|287063|十年|[2116, 1234]|[陈奕迅, 合唱]|28523|黑白灰|https://p1.music.126.net/c.jpg|null|null",
            WyParser.parseSongDetail("287063", json)?.fmt(),
        )
    }

    @Test
    fun `parse song detail without album`() {
        val json = """{"songs":[{"name":"无专辑","id":1,"artists":[{"id":9,"name":"某人"}]}],"code":200}"""
        assertEquals(
            "WY|287063|无专辑|[9]|[某人]|null|null|null|null|null",
            WyParser.parseSongDetail("287063", json)?.fmt(),
        )
    }

    @Test
    fun `parse song detail returns null when album has no picture or no artists`() {
        val noPic = """{"songs":[{"name":"无封面","id":1,"artists":[{"id":9,"name":"某人"}],"album":{"id":3,"name":"a"}}],"code":200}"""
        val noArtist = """{"songs":[{"name":"无歌手","id":1,"artists":[],"album":{"id":3,"name":"a","picUrl":"http://x"}}],"code":200}"""
        assertNull(WyParser.parseSongDetail("287063", noPic))
        assertNull(WyParser.parseSongDetail("287063", noArtist))
        assertNull(WyParser.parseSongDetail("287063", ""))
    }

    @Test
    fun `parse lyric`() {
        val json = """{"lrc":{"version":5,"lyric":"[00:00.00]十年\n[00:01.00]陈奕迅"},"code":200}"""
        assertEquals("[00:00.00]十年\n[00:01.00]陈奕迅", WyParser.parseLyric(json))
        assertEquals("", WyParser.parseLyric("""{"nolyric":true,"code":200}"""))
        assertEquals("", WyParser.parseLyric(""))
    }

    @Test
    fun `parse play url`() {
        val json = """{"data":[{"id":287063,"url":"http://m701.music.126.net/x/y.flac","br":999000,"size":123,"type":"flac"}],"code":200}"""
        val noUrl = """{"data":[{"id":287063,"url":null,"br":0,"size":0,"type":null}],"code":200}"""
        assertEquals("http://m701.music.126.net/x/y.flac", WyParser.parsePlayUrl(json))
        assertNull(WyParser.parsePlayUrl(noUrl))
        assertNull(WyParser.parsePlayUrl("""{"data":[],"code":200}"""))
        assertNull(WyParser.parsePlayUrl(""))
    }
}
