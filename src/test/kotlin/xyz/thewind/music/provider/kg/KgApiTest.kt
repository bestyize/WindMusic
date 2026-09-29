package xyz.thewind.music.provider.kg

import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

/** 期望值均由旧版 Java 代码（RequestModule / KgMusicModel）以相同输入运行得到。 */
class KgApiTest {
    private val requestId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")

    @Test
    fun `sign sorts keys and wraps with key`() {
        val params = HashMap<String, Any?>(mapOf("b" to "2", "a" to "1", "c" to "x"))
        assertEquals("3d8926d8011bd03d3304460874b07092", KgApi.sign("abc", params))
    }

    @Test
    fun `search url matches legacy param order and signature`() {
        assertEquals(
            "https://complexsearch.kugou.com/v2/search/song?signature=cea3bd3c1006c27f1e4824d26329ee7d&area_code=0&mid=0" +
                "&privilegefilter=0&userid=0&uuid=0&platform=WebFilter&token=&srcappid=2919&dfid=0&iscorrection=1" +
                "&requestid=123e4567-e89b-12d3-a456-426614174000&pagesize=10&clientver=2000&page=1" +
                "&clienttime=1700000000123&keyword=周杰伦",
            KgApi.buildSearchUrl("周杰伦", 10, 0, 1700000000123L, requestId),
        )
        assertEquals(
            "https://complexsearch.kugou.com/v2/search/song?signature=2b74ca8f30ef0749622bf7c4b708d019&area_code=0&mid=0" +
                "&privilegefilter=0&userid=0&uuid=0&platform=WebFilter&token=&srcappid=2919&dfid=0&iscorrection=1" +
                "&requestid=123e4567-e89b-12d3-a456-426614174000&pagesize=30&clientver=2000&page=3" +
                "&clienttime=1659862058062&keyword=hello world",
            KgApi.buildSearchUrl("hello world", 30, 2, 1659862058062L, requestId),
        )
    }

    @Test
    fun `download url matches legacy with account`() {
        assertEquals(
            "https://gateway.kugou.com/v3/url?signature=70be626aeab2f6e788f87fdc1456a549&module=&area_code=1&mid=0" +
                "&vipType=6&pid=2&ptype=0&uuid=0&version=10479&userid=1538743078&token=fa044ef0ad5e&pidversion=3001" +
                "&mtype=2&dfid=-&appid=1005&album_audio_id=album_audio_id&album_id=2900867&clientver=10479&cmd=26" +
                "&behavior=play&clienttime=1700000000&hash=0CCA78AF607DC52400D31C18BF036277" +
                "&key=70d3965f31ea222582a898ed5c3b90a5",
            KgApi.buildDownloadUrl("0CCA78AF607DC52400D31C18BF036277", "1538743078", "fa044ef0ad5e", 1700000000L),
        )
    }

    @Test
    fun `download url writes literal null when account is missing`() {
        assertEquals(
            "https://gateway.kugou.com/v3/url?signature=2c4874c9a4be748552dfad2265816657&module=&area_code=1&mid=0" +
                "&vipType=6&pid=2&ptype=0&uuid=0&version=10479&userid=null&token=null&pidversion=3001" +
                "&mtype=2&dfid=-&appid=1005&album_audio_id=album_audio_id&album_id=2900867&clientver=10479&cmd=26" +
                "&behavior=play&clienttime=1700000000&hash=0CCA78AF607DC52400D31C18BF036277" +
                "&key=f6f57d8a5844579c36a5237ab81ec369",
            KgApi.buildDownloadUrl("0CCA78AF607DC52400D31C18BF036277", null, null, 1700000000L),
        )
    }

    @Test
    fun `parse download url`() {
        assertEquals(
            "http://fs.youthandroid.kugou.com/abc/x.mp3",
            KgApi.parseDownloadUrl(
                "{\"status\":1,\"url\":[\"http:\\/\\/fs.youthandroid.kugou.com\\/abc\\/x.mp3\",\"http:\\/\\/b\"],\"x\":1}",
            ),
        )
        assertEquals("", KgApi.parseDownloadUrl("{\"status\":2,\"error\":\"need login\"}"))
        // 旧版行为：含 http: 但没有 url 数组时截出无意义的片段
        assertEquals(":{", KgApi.parseDownloadUrl("{\"data\":{\"play\":\"http://a.b/c.mp3\",\"x\":\"y\"}}"))
        // 旧版在这里抛 StringIndexOutOfBoundsException，新版返回空串
        assertEquals("", KgApi.parseDownloadUrl("{\"u\":\"http:"))
    }

    @Test
    fun `song detail url uses first hash uppercased`() {
        assertEquals(
            "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=0CCA78AF607DC52400D31C18BF036277&mid=12345",
            KgApi.songDetailUrl("0cca78af607dc52400d31c18bf036277"),
        )
    }
}
