package xyz.thewind.music.service

import org.junit.jupiter.api.Test
import xyz.thewind.music.common.Codec
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.model.PlayerBean
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.support.FakeMusicProvider
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MusicServiceTest {
    private val provider = FakeMusicProvider()
    private val service = MusicService(listOf(provider))

    @Test
    fun `unknown source yields empty results`() {
        assertTrue(service.search("NOPE", "晴天", 3, 0).isEmpty())
        assertTrue(service.searchWithDownloadLinks(null, "晴天", 3).isEmpty())
        assertEquals("", service.getLyric("NOPE", "1"))
        assertNull(service.getSongDetail("NOPE", "1"))
        val song = service.resolveDownloadLink("NOPE", "1", "PQ")
        assertEquals("NOPE", song.songSrc)
        assertEquals("1", song.songId)
        assertTrue(song.downloadLinkMap.isEmpty())
    }

    @Test
    fun `dispatches to provider by source`() {
        assertEquals(3, service.search("TEST", "晴天", 3, 1).size)
        val song = service.resolveDownloadLink("TEST", "42", "SQ")
        assertEquals("https://example.com/42.SQ.mp3", song.downloadLinkMap["SQ"])
    }

    @Test
    fun `batch download decodes base64 json and tolerates null link map`() {
        val json = """[{"songId":"1","quality":"HQ","downloadLinkMap":null},{"songId":"2"}]"""
        val songs = service.resolveDownloadLinks("TEST", Codec.base64Encode(json))
        assertEquals(listOf("1", "2"), songs.map { it.songId })
        assertEquals("https://example.com/1.HQ.mp3", songs[0].downloadLinkMap["HQ"])
        assertEquals("https://example.com/2.PQ.mp3", songs[1].downloadLinkMap["PQ"])
        assertTrue(service.resolveDownloadLinks("TEST", "not base64 !!").isEmpty())
    }

    @Test
    fun `player info falls back to lyric header when detail is missing`() {
        assertEquals(
            listOf(PlayerBean("晴天", "周杰伦", "img/wind-icon-256.png", "https://example.com/7.PQ.mp3", provider.lyric)),
            service.getPlayerInfo("TEST_7"),
        )
    }

    @Test
    fun `player info prefers song detail`() {
        provider.detail = SongBean(songName = "七里香", singersName = listOf("周杰伦", "方文山"), picUrl = "https://p/cover.jpg")
        val info = service.getPlayerInfo("TEST_7").single()
        assertEquals("七里香", info.title)
        assertEquals("周杰伦&方文山", info.author)
        assertEquals("https://p/cover.jpg", info.pic)
    }

    @Test
    fun `invalid share id yields empty list`() {
        assertTrue(service.getPlayerInfo(null).isEmpty())
        assertTrue(service.getPlayerInfo("TEST").isEmpty())
        assertTrue(service.getPlayerInfo("TEST_1_2").isEmpty())
    }

    @Test
    fun `lyric header parsing matches legacy line offsets`() {
        val kg = "[id:abc]\n[ti:晴天]\n[ar:周杰伦]"
        assertEquals("晴天", LyricHeader.songName("KG", kg))
        assertEquals("周杰伦", LyricHeader.singerName("KG", kg))
        assertEquals("故事的小黄花", LyricHeader.line("[00:01.00]故事的小黄花", 0))
        assertEquals("", LyricHeader.line("", 3))
    }

    @Test
    fun `song bean gson round trip keeps downloadLinkMap field name`() {
        val json = GsonUtil.toJson(SongBean(songId = "1").apply { downloadLinkMap["PQ"] = "u" })
        assertEquals("""{"songId":"1","downloadLinkMap":{"PQ":"u"}}""", json)
    }
}
