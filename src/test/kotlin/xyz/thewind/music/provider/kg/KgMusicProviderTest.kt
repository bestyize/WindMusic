package xyz.thewind.music.provider.kg

import org.junit.jupiter.api.Test
import xyz.thewind.music.model.MusicSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 期望值均由旧版 Java 代码（KgMusicModel / SongDetail / MobSongDetail）以相同输入运行得到。 */
class KgMusicProviderTest {

    @Test
    fun `select hash by quality like legacy`() {
        val cases = listOf(
            Triple("pq-hq-sq-zq", "ZQ", "zq"),
            Triple("pq-hq-sq-zq", "SQ", "pq"), // 旧版行为：4 段时 SQ 退回 PQ
            Triple("pq-hq-sq-zq", "HQ", "hq"),
            Triple("pq-hq-sq-zq", "PQ", "pq"),
            Triple("pq-hq-sq-", "SQ", "sq"),
            Triple("pq-hq-sq-", "ZQ", "pq"),
            Triple("pq-hq--", "HQ", "hq"),
            Triple("pq-hq--", "SQ", "pq"),
            Triple("pq---", "HQ", "pq"),
            Triple("single", "HQ", "single"),
            Triple("pq--sq-", "HQ", ""),
        )
        for ((songId, quality, expected) in cases) {
            assertEquals(expected, KgMusicProvider.selectHash(songId, quality), "$songId $quality")
        }
    }

    @Test
    fun `parse search result`() {
        val json = "{\"error_msg\":\"\",\"data\":{\"lists\":[{\"SongName\":\"稻香\",\"AlbumName\":\"魔杰座\",\"AlbumID\":\"960399\"," +
            "\"ResFileHash\":\"\",\"SQFileHash\":\"811DBD4CDB806D009D7026A4C3FFF0F4\",\"HQFileHash\":\"5DD1C4A1A5E1B7B1C6A2E4E6E3A5B4C2\"," +
            "\"FileHash\":\"0A62227CAAB66F54D43EC084B4BDD81F\",\"Singers\":[{\"name\":\"周杰伦\",\"id\":3520}],\"mvdata\":[{\"hash\":\"MVHASH1\"}]}," +
            "{\"SongName\":\"x\",\"FileHash\":\"AA\",\"Singers\":[{\"name\":\"A\",\"id\":1},{\"name\":\"B\",\"id\":2}],\"mvdata\":[]}]}}"
        val songs = KgMusicProvider.parseSearchResult(json)
        assertEquals(2, songs.size)

        val first = songs[0]
        assertEquals(MusicSource.KG, first.songSrc)
        assertEquals(
            "0A62227CAAB66F54D43EC084B4BDD81F-5DD1C4A1A5E1B7B1C6A2E4E6E3A5B4C2-811DBD4CDB806D009D7026A4C3FFF0F4-",
            first.songId,
        )
        assertEquals("稻香", first.songName)
        assertEquals(listOf("3520"), first.singersId)
        assertEquals(listOf("周杰伦"), first.singersName)
        assertEquals("960399", first.albumId)
        assertEquals("魔杰座", first.albumName)
        assertEquals("SQ", first.quality)
        assertEquals("MVHASH1", first.mvUrl)

        val second = songs[1]
        assertEquals("AA-null-null-null", second.songId)
        assertEquals("x", second.songName)
        assertEquals(listOf("1", "2"), second.singersId)
        assertEquals(listOf("A", "B"), second.singersName)
        assertNull(second.albumId)
        assertNull(second.albumName)
        assertEquals("PQ", second.quality)
        assertEquals("", second.mvUrl)
    }

    @Test
    fun `parse search result tolerates bad responses`() {
        assertTrue(KgMusicProvider.parseSearchResult("").isEmpty())
        assertTrue(KgMusicProvider.parseSearchResult("not json").isEmpty())
        assertTrue(KgMusicProvider.parseSearchResult("{\"error_msg\":\"err\"}").isEmpty())
    }

    @Test
    fun `parse song detail`() {
        val songId = "0cca78af607dc52400d31c18bf036277-hq-sq-"
        val json = "{\"status\":1,\"data\":{\"hash\":\"0CCA78AF607DC52400D31C18BF036277\",\"audio_name\":\"周杰伦 - 稻香\"," +
            "\"img\":\"http://imge.kugou.com/stdmusic/480/x.jpg\",\"album_name\":\"魔杰座\",\"album_id\":\"960399\"," +
            "\"authors\":[{\"author_id\":\"3520\",\"author_name\":\"周杰伦\"},{\"author_id\":\"1\",\"author_name\":\"B\"}]}}"
        val song = assertNotNull(KgMusicProvider.parseSongDetail(songId, json))
        assertEquals(MusicSource.KG, song.songSrc)
        assertEquals(songId, song.songId)
        assertEquals("周杰伦 - 稻香", song.songName)
        assertEquals("http://imge.kugou.com/stdmusic/480/x.jpg", song.picUrl)
        assertEquals(listOf("3520", "1"), song.singersId)
        assertEquals(listOf("周杰伦", "B"), song.singersName)
        assertEquals("魔杰座", song.albumName)
        assertEquals("960399", song.albumId)
    }

    @Test
    fun `parse song detail returns null without authors`() {
        assertNull(KgMusicProvider.parseSongDetail("x".repeat(32), "{\"status\":0}"))
        assertNull(KgMusicProvider.parseSongDetail("x".repeat(32), "{\"status\":0,\"data\":[]}"))
    }
}
