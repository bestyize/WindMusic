package xyz.thewind.music.provider.mg

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** 期望值均由旧版 Java 实现（MgEncrypt）在相同输入下跑出。 */
class MgApiTest {
    private val keyword = "周杰伦 晴天"
    private val sid = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
    private val deviceId = "0123456789ABCDEF0123456789ABCDEF"
    private val msisdn = "MTg4MTIzNDU2Nzg=" // Base64("18812345678")
    private val timestamp = "1700000000123"

    @Test
    fun `sign matches legacy`() {
        assertEquals("40b2e5fc67d69b04917224d3aef57e99", MgApi.sign(keyword, deviceId, msisdn, timestamp))
    }

    @Test
    fun `search url matches legacy including parameter order and trailing ampersand`() {
        val expected = "https://jadeite.migu.cn/music_search/v3/search/searchAll?feature=1011000000&pageNo=1&pageSize=20" +
            "&sort=0&text=%E5%91%A8%E6%9D%B0%E4%BC%A6+%E6%99%B4%E5%A4%A9" +
            "&searchSwitch=%7B%22song%22%3A1%2C%22album%22%3A0%2C%22singer%22%3A0%2C%22tagSong%22%3A1%2C%22mvSong%22%3A0" +
            "%2C%22bestShow%22%3A0%2C%22songlist%22%3A0%2C%22lyricSong%22%3A0%7D" +
            "&sid=$sid&isCopyright=1&isCorrect=1&"
        assertEquals(expected, MgApi.searchUrl(keyword, num = 20, page = 1, sid = sid))
    }

    @Test
    fun `search headers match legacy`() {
        val headers = MgApi.searchHeaders(keyword, msisdn, deviceId, timestamp)
        val expected = mapOf(
            "os" to "android 12",
            "ip" to "MTE0Ljg4LjE2MS41MQ==",
            "sign" to "40b2e5fc67d69b04917224d3aef57e99",
            "uiVersion" to "A_music_3.12.4",
            "ua" to "Android_migu",
            "deviceId" to deviceId,
            "version" to "7.21.0",
            "mgm-Network-type" to "04",
            "mode" to "android",
            "appId" to "yyapp2",
            "android_id" to "afad815163f1d394",
            "msisdn" to msisdn,
            "timestamp" to timestamp,
        )
        assertEquals(expected, headers)
        // 旧版 HashMap 的遍历顺序
        assertEquals(expected.keys.toList(), headers.keys.toList())
    }

    @Test
    fun `random phone number and device id match legacy for the same seed`() {
        assertEquals("18850005476", MgApi.randomPhoneNumber(1700000000000L))
        assertEquals("18803840558", MgApi.randomPhoneNumber(42L))
        assertEquals("3E1264C60704D763219205884509F439", MgApi.randomDeviceId(32, 1700000000000L))
        assertEquals("9A0B990B17DB8D8D386E1A6F21732EAD", MgApi.randomDeviceId(32, 42L))
    }

    @Test
    fun `sid is two dashless uuids`() {
        assertEquals(64, MgApi.randomSid().length)
        assertTrue(MgApi.randomSid().all { it in '0'..'9' || it in 'a'..'f' })
    }
}
