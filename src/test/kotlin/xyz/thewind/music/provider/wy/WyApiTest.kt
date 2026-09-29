package xyz.thewind.music.provider.wy

import org.junit.jupiter.api.Test
import xyz.thewind.music.model.Quality
import kotlin.test.assertEquals

/** 期望值由旧版 Java 代码（WyMusicModel 的 URL / Cookie 拼接）对同样的输入运行得出。 */
class WyApiTest {

    @Test
    fun `search url encodes keyword and computes offset`() {
        assertEquals(
            "http://music.163.com/api/cloudsearch/get/web?s=%E9%99%88%E5%A5%95%E8%BF%85+%E5%8D%81%E5%B9%B4%26a%3Db&type=1&offset=40&limit=20&total=true",
            WyApi.searchUrl("陈奕迅 十年&a=b", 20, 2),
        )
    }

    @Test
    fun `play url`() {
        assertEquals(
            "http://music.163.com/api/song/enhance/player/url?ids=%5B287063%5D&br=999000",
            WyApi.playUrlUrl("287063", WyApi.bitRateOf(Quality.SQ)),
        )
        assertEquals(
            "http://music.163.com/api/song/enhance/player/url?ids=%5Bnull%5D&br=128000",
            WyApi.playUrlUrl(null, WyApi.bitRateOf("unknown")),
        )
    }

    @Test
    fun `bit rates`() {
        assertEquals(
            listOf("1999000", "999000", "320000", "192000", "128000", "128000"),
            listOf(Quality.ZQ, Quality.SQ, Quality.HQ, Quality.PQ, Quality.LQ, Quality.EQ).map(WyApi::bitRateOf),
        )
    }

    @Test
    fun `lyric and detail urls`() {
        assertEquals("https://music.163.com/api/song/lyric?lv=0&kv=0&tv=0&id=287063", WyApi.lyricUrl("287063"))
        assertEquals("https://music.163.com/api/song/detail?ids=%5b287063%5d", WyApi.songDetailUrl("287063"))
    }

    @Test
    fun `play cookie falls back to default only for empty token`() {
        assertEquals(
            "MUSIC_U=41d12a493b80de6fa49d16f6570d469a23f84bcf3394fedda977317d531e9c861d1b60489766205edfc3c0ec8cbcadd641049cea1c6bb9b6",
            WyApi.playCookie(""),
        )
        assertEquals("MUSIC_U=null", WyApi.playCookie(null))
        assertEquals("MUSIC_U=abc", WyApi.playCookie("abc"))
    }
}
