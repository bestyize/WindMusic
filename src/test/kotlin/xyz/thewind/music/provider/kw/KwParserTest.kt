package xyz.thewind.music.provider.kw

import org.junit.jupiter.api.Test
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.provider.kw.dto.KwSearchSong
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 期望值均由旧版 Java 代码生成 */
class KwParserTest {

    @Test
    fun `convertToLyricTime golden values`() {
        val cases = listOf(
            null to "",
            "" to "",
            "123" to "",
            "1.2" to "",
            "12.3" to "",
            "12.34" to "00:12.34",
            "0.00" to "00:00.00",
            "65.123" to "01:05.12",
            "3600.5" to "",
            "3600.55" to "60:00.55",
            "9.99" to "00:09.99",
            "59.999" to "00:59.99",
            "-1.23" to "00:0-1.23",
            "+5.12" to "00:05.12",
            "1.2.345" to "00:01.2.",
            "12.3.45" to "00:12.3.",
            "599.999" to "09:59.99",
            "6000.01" to "100:00.01",
        )
        for ((input, expected) in cases) {
            assertEquals(expected, KwParser.convertToLyricTime(input), "input = $input")
        }
    }

    @Test
    fun `convertToLyricTime throws on non-numeric seconds`() {
        for (input in listOf("abc.de", ".123", "1x.23")) {
            assertFailsWith<NumberFormatException>("input = $input") { KwParser.convertToLyricTime(input) }
        }
    }

    @Test
    fun `parseLyric builds lrc lines`() {
        val json = """{"data":{"lrclist":[{"lineLyric":"故事的小黄花","time":"12.34"},{"lineLyric":"从出生那年就飘着","time":"65.123"},{"time":"3.5"},{"lineLyric":"x"}]}}"""
        assertEquals("[00:12.34]故事的小黄花\n[01:05.12]从出生那年就飘着\n[]null\n[]x\n", KwParser.parseLyric(json))
    }

    @Test
    fun `parseLyric keeps lines before a malformed time`() {
        val json = """{"data":{"lrclist":[{"lineLyric":"a","time":"1.00"},{"lineLyric":"b","time":"ab.cd"},{"lineLyric":"c","time":"2.00"}]}}"""
        assertEquals("[00:01.00]a\n", KwParser.parseLyric(json))
    }

    @Test
    fun `parseLyric returns empty for missing data`() {
        for (json in listOf("""{"data":null}""", """{"data":{}}""", "{}", "", "not json")) {
            assertEquals("", KwParser.parseLyric(json), "json = $json")
        }
    }

    @Test
    fun `search song quality takes the highest downloadable level`() {
        val cases = listOf(
            null to Quality.LQ,
            "" to Quality.LQ,
            "level:hr,bitrate:4000,format:flac,size:53.12Mb;level:ff,bitrate:2000,format:flac,size:27.29Mb;level:p,bitrate:320,format:mp3,size:10.14Mb" to Quality.ZQ,
            "level:ff,bitrate:2000" to Quality.SQ,
            "level:p" to Quality.HQ,
            "level:h" to Quality.PQ,
            "level:s" to Quality.LQ,
            "bitrate:128,level:h,level:p" to Quality.HQ,
            "level:ff:x" to Quality.LQ,
            "size:1Mb;level:hr" to Quality.ZQ,
            // 2026 年酷我在前面加了加密格式的音质，实际响应截取
            "level:bcms,bitrate:22000,format:mgg,size:22.11Mb;level:zply,bitrate:20900,format:mflac,size:178.32Mb;" +
                "level:ff,bitrate:2000,format:flac,size:52.83Mb;level:p,bitrate:320,format:mp3,size:10.29Mb;" +
                "level:h,bitrate:128,format:mp3,size:4.12Mb;level:s,bitrate:48,format:aac,size:1.57Mb" to Quality.SQ,
        )
        for ((minInfo, expected) in cases) {
            assertEquals(expected, KwSearchSong().apply { this.minInfo = minInfo }.quality, "minInfo = $minInfo")
        }
    }

    @Test
    fun `search song cover prefers album picture over mv still`() {
        val song = KwSearchSong().apply { mvPic = "https://img2.kuwo.cn/wmvpic/1.jpg" }
        assertEquals("https://img2.kuwo.cn/wmvpic/1.jpg", song.picUrl)
        song.albumPicPath = "120/s4s99/74/3451932382.jpg"
        assertEquals("https://img1.kuwo.cn/star/albumcover/300/s4s99/74/3451932382.jpg", song.picUrl)
        assertNull(KwSearchSong().apply { mvPic = "" }.picUrl)
    }

    @Test
    fun `search song splits singers like Java split`() {
        val cases = listOf(
            "A&B&C" to listOf("A", "B", "C"),
            "A&&B" to listOf("A", "", "B"),
            "A&" to listOf("A"),
            "" to listOf(""),
            "&A" to listOf("", "A"),
            "周杰伦" to listOf("周杰伦"),
        )
        for ((raw, expected) in cases) {
            val song = KwSearchSong().apply {
                singerId = raw
                singerName = raw
            }
            assertEquals(expected, song.singersId, "singerId = $raw")
            assertEquals(expected, song.singersName, "singerName = $raw")
        }
        assertEquals(emptyList(), KwSearchSong().singersId)
        assertEquals(emptyList(), KwSearchSong().singersName)
    }

    @Test
    fun `parseSearch maps fields`() {
        val json = """
            {"abslist":[{"DC_TARGETID":"80488731","SONGNAME":"晴天","ALBUMID":"3265","ALBUM":"叶惠美",
            "ARTIST":"周杰伦&五月天","ARTISTID":"336&1234","hts_MVPIC":"https://img.kuwo.cn/1.jpg",
            "N_MINFO":"level:ff,bitrate:2000,format:flac,size:27.29Mb;level:p,bitrate:320,format:mp3,size:10.14Mb"}]}
        """.trimIndent()
        val songs = KwParser.parseSearch(json)
        assertEquals(1, songs.size)
        val song = songs[0]
        assertEquals(MusicSource.KW, song.songSrc)
        assertEquals("80488731", song.songId)
        assertEquals("晴天", song.songName)
        assertEquals(listOf("336", "1234"), song.singersId)
        assertEquals(listOf("周杰伦", "五月天"), song.singersName)
        assertEquals("3265", song.albumId)
        assertEquals("叶惠美", song.albumName)
        assertEquals(Quality.SQ, song.quality)
        assertEquals("https://img.kuwo.cn/1.jpg", song.picUrl)
        assertTrue(song.downloadLinkMap.isEmpty())
    }

    @Test
    fun `parseSearch returns empty on bad input`() {
        assertTrue(KwParser.parseSearch("").isEmpty())
        assertTrue(KwParser.parseSearch("not json").isEmpty())
        assertTrue(KwParser.parseSearch("{}").isEmpty())
        assertTrue(KwParser.parseSearch("""{"abslist":null}""").isEmpty())
    }

    @Test
    fun `parsePlayUrl reads data url`() {
        assertEquals(
            "http://sycdn.kuwo.cn/x/1.flac",
            KwParser.parsePlayUrl("""{"code":200,"data":{"bitrate":2000,"format":"flac","url":"http://sycdn.kuwo.cn/x/1.flac"}}"""),
        )
        assertNull(KwParser.parsePlayUrl("""{"code":200}"""))
        assertNull(KwParser.parsePlayUrl("""{"code":200,"data":null}"""))
        assertNull(KwParser.parsePlayUrl(""))
    }

    @Test
    fun `parseSongDetail maps fields`() {
        val json = """{"code":200,"data":{"albumid":3265,"hasLossless":true,"artist":"周杰伦","pic":"http://img1.kwcdn.kuwo.cn/star/albumcover/500/1.jpg","songId":80488731,"duration":269,"artistid":336,"album":"叶惠美","name":"晴天"}}"""
        val song = KwParser.parseSongDetail("80488731", json)!!
        assertEquals(MusicSource.KW, song.songSrc)
        assertEquals("80488731", song.songId)
        assertEquals("晴天", song.songName)
        assertEquals(listOf("336"), song.singersId)
        assertEquals("https://img1.kwcdn.kuwo.cn/star/albumcover/500/1.jpg", song.picUrl)
        assertEquals("叶惠美", song.albumName)
        assertEquals(Quality.SQ, song.quality)
        assertEquals("3265", song.albumId)
        assertEquals(listOf("周杰伦"), song.singersName)
    }

    @Test
    fun `parseSongDetail with sparse data`() {
        val song = KwParser.parseSongDetail("1", """{"data":{"name":"n"}}""")!!
        assertEquals("n", song.songName)
        assertEquals(listOf("0"), song.singersId)
        assertNull(song.picUrl)
        assertEquals(Quality.HQ, song.quality)
        assertEquals("0", song.albumId)
        // 旧版为 [null]，这里改为空列表
        assertEquals(emptyList(), song.singersName)
    }

    @Test
    fun `parseSongDetail returns null without data`() {
        assertNull(KwParser.parseSongDetail("1", """{"code":200}"""))
        assertNull(KwParser.parseSongDetail("1", ""))
        assertNull(KwParser.parseSongDetail("1", "not json"))
    }
}
