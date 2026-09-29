package xyz.thewind.music.provider.mg

import org.junit.jupiter.api.Test
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MgMusicProviderParseTest {
    private val provider = MgMusicProvider()

    private val searchJson = """
        {"code":"000000",
         "songResultData":{"resultList":[[{"copyrightId":"normal","name":"不应被使用"}]]},
         "tagSongResultData":{"resultList":[
           [{"copyrightId":"1","name":"晴天","singerList":[{"id":"112","name":"周杰伦"},{"id":"113","name":"Lara"}],
             "albums":[{"id":"A1","name":"叶惠美"}],"lrcUrl":"","lyricUrl":"http://lrc/1",
             "img3":"prod/cover.jpg","imgItems":[{"img":"http://img/ignored.jpg"}],
             "mvId":"","mvList":[{"id":"x","copyrightId":"MV1"}],
             "newRateFormats":[{"formatType":"PQ"},{"formatType":"HQ"},{"formatType":"SQ"}]},
            {"copyrightId":"1-other-version","name":"晴天 (Live)"}],
           [{"copyrightId":"2","name":"B","singers":[{"id":"9","name":"X"}],
             "album":"AlbumB","albumId":"AB","albums":[{"id":"no","name":"no"}],
             "lrcUrl":"http://lrc/2","lyricUrl":"http://lrc/ignored",
             "imgItems":[{"img":"http://img/2.jpg"}],"picUrl":"http://pic/ignored",
             "mvId":"MV2","mvList":[{"copyrightId":"no"}],
             "newRateFormats":[{},{}],"audioFormats":[{},{},{},{}]}],
           [{"copyrightId":"3","name":"C","picUrl":"http://pic/3","z3dCode":{"formatType":"Z3D"}}],
           [{"copyrightId":"4","newRateFormats":[{},{}]}],
           [{"copyrightId":"5","newRateFormats":[{},{},{},{},{}]}],
           [{"copyrightId":"6","newRateFormats":[{}]}]
         ]}}
    """.trimIndent()

    @Test
    fun `search result prefers tag songs and takes first version of each group`() {
        val songs = provider.parseSearchResult(searchJson)
        assertEquals(listOf("1", "2", "3", "4", "5", "6"), songs.map { it.songId })
        assertTrue(songs.all { it.songSrc == MusicSource.MG })

        val first = songs[0]
        assertEquals("晴天", first.songName)
        assertEquals(listOf("112", "113"), first.singersId)
        assertEquals(listOf("周杰伦", "Lara"), first.singersName)
        assertEquals("A1", first.albumId)
        assertEquals("叶惠美", first.albumName)
        assertEquals("http://lrc/1", first.lrcUrl)
        assertEquals("https://d.musicapp.migu.cn/prod/cover.jpg", first.picUrl)
        assertEquals("MV1", first.mvUrl)
        assertEquals(Quality.SQ, first.quality)

        val second = songs[1]
        assertEquals(listOf("9"), second.singersId)
        assertEquals(listOf("X"), second.singersName)
        assertEquals("AB", second.albumId)
        assertEquals("AlbumB", second.albumName)
        assertEquals("http://lrc/2", second.lrcUrl)
        assertEquals("http://img/2.jpg", second.picUrl)
        assertEquals("MV2", second.mvUrl)
        // audioFormats 非空时以它的数量为准
        assertEquals(Quality.ZQ, second.quality)

        val third = songs[2]
        assertEquals("", third.albumId)
        assertEquals("", third.albumName)
        assertEquals("http://pic/3", third.picUrl)
        assertNull(third.lrcUrl)
        assertNull(third.mvUrl)
        assertEquals(emptyList(), third.singersName)
        assertEquals(Quality.EQ, third.quality)

        assertEquals(listOf(Quality.HQ, Quality.EQ, Quality.PQ), songs.drop(3).map { it.quality })
    }

    @Test
    fun `search result falls back to normal songs when tag songs are empty`() {
        val json = """{"songResultData":{"resultList":[[{"copyrightId":"n1","name":"N"}]]},"tagSongResultData":{"resultList":[]}}"""
        val songs = provider.parseSearchResult(json)
        assertEquals(listOf("n1"), songs.map { it.songId })
        assertEquals(Quality.PQ, songs[0].quality)
    }

    @Test
    fun `search result tolerates empty or broken responses`() {
        assertEquals(emptyList(), provider.parseSearchResult(""))
        assertEquals(emptyList(), provider.parseSearchResult("not json"))
        assertEquals(emptyList(), provider.parseSearchResult("""{"tagSongResultData":null,"songResultData":null}"""))
        assertEquals(listOf("ok"), provider.parseSearchResult("""{"songResultData":{"resultList":[[],null,[{"copyrightId":"ok"}]]}}""").map { it.songId })
    }

    /** 期望值由旧版 MgDownloadBean + Gson 在同一 JSON 上跑出。 */
    @Test
    fun `resource info matches legacy`() {
        val json = """{"code":"000000","resource":[{"songName":"晴天","singerId":"112","singer":"周杰伦","album":"叶惠美","albumId":"1",""" +
            """"albumImgs":[{"imgSizeType":"01","img":"http://img/a.jpg"},{"img":"http://img/b.jpg"}],"lrcUrl":"http://lrc/x.lrc",""" +
            """"newRateFormats":[{"formatType":"PQ","url":"ftp://218.200.160.122:21/public/product9th/a.mp3"},""" +
            """{"formatType":"HQ","androidUrl":"ftp://x/public/b.mp3"},{"formatType":"SQ","url":"http://keep.me/sq.flac"}],""" +
            """"z3dCode":{"formatType":"Z3D","androidUrl":"ftp://y/public/z3d.flac"}}]}"""
        val song = provider.parseResource("600", json)
        assertEquals(MusicSource.MG, song.songSrc)
        assertEquals("600", song.songId)
        assertEquals(
            mapOf(
                "PQ" to "http://freetyst.nf.migu.cn/public/product9th/a.mp3",
                "HQ" to "http://freetyst.nf.migu.cn/public/b.mp3",
                "SQ" to "http://keep.me/sq.flac",
                "EQ" to "http://freetyst.nf.migu.cn/public/z3d.flac",
            ),
            song.downloadLinkMap,
        )
        assertEquals("http://lrc/x.lrc", song.lrcUrl)
        assertEquals("http://img/a.jpg", song.picUrl)
        assertEquals("晴天", song.songName)
        assertEquals("叶惠美", song.albumName)
        assertEquals(listOf("周杰伦"), song.singersName)
        assertEquals(listOf("112"), song.singersId)
    }

    @Test
    fun `resource info failure returns bare song`() {
        for (json in listOf("", "oops", """{"resource":[]}""", """{"resource":null}""")) {
            val song = provider.parseResource("600", json)
            assertEquals("600", song.songId)
            assertEquals(MusicSource.MG, song.songSrc)
            assertNull(song.songName)
            assertTrue(song.downloadLinkMap.isEmpty())
        }
    }
}
