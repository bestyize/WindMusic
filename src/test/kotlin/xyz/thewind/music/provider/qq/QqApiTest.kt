package xyz.thewind.music.provider.qq

import org.junit.jupiter.api.Test
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.qq.dto.QqLoginResponse
import xyz.thewind.music.provider.qq.dto.QqLoginToken
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** 期望值均由旧版 Java 代码对相同输入运行得到。 */
class QqApiTest {

    private fun token() = QqLoginToken().apply {
        musicKey = "Q_H_L_5abc<def>=&x=="
        musicId = "1152921504741490647"
        accessToken = ""
        refreshToken = "59_qz4-IC7oLI7GSx"
        openid = "opCFJw8b_qxl5vdj"
        unionid = "oqFLxslbFUZPalHg"
        expiredAt = 1957831066L
        musicKeyCreateTime = 1700000000L
    }

    private fun song(songId: String, quality: String) =
        SongBean(songSrc = MusicSource.QQ, songId = songId, quality = quality)

    @Test
    fun `vkey request body without token`() {
        val body = QqApi.vkeyRequestBody(listOf(song("003OUlho2HcRHC<>003OUlho2HcRHC", Quality.SQ)), null)
        assertEquals(
            """{"comm":{"tmeAppID":"qqmusic","tmeLoginType":1,"wid":"0","ct":"19","cv":"1873"},"queryvkey":{"method":"CgiGetVkey","module":"vkey.GetVkeyServer","param":{"checklimit":0,"ctx":1,"downloadfrom":0,"filename":["F000003OUlho2HcRHC.flac"],"guid":"810BA5E07103080839C18EE1FD2EE9B5","musicfile":["F000003OUlho2HcRHC.flac"],"nettype":"","referer":"y.qq.com","scene":0,"songmid":["003OUlho2HcRHC"],"songtype":[1]}}}""",
            body,
        )
    }

    @Test
    fun `vkey request body with token uses html escaping gson`() {
        val songs = listOf(
            song("001abc<>002def", Quality.ZQ),
            song("0033x<>004media", Quality.LQ),
            song("m<>n", Quality.PQ),
            song("h<>i", Quality.HQ),
        )
        assertEquals(
            """{"comm":{"authst":"Q_H_L_5abc\u003cdef\u003e\u003d\u0026x\u003d\u003d","tmeAppID":"qqmusic","tmeLoginType":1,"uin":"1152921504741490647","wid":"0","ct":"19","cv":"1873"},"queryvkey":{"method":"CgiGetVkey","module":"vkey.GetVkeyServer","param":{"checklimit":0,"ctx":1,"downloadfrom":0,"filename":["RS01002def.flac","C400004media.m4a","M500n.mp3","M800i.mp3"],"guid":"810BA5E07103080839C18EE1FD2EE9B5","musicfile":["RS01001abc.flac","C4000033x.m4a","M500m.mp3","M800h.mp3"],"nettype":"","referer":"y.qq.com","scene":0,"songmid":["001abc","0033x","m","h"],"songtype":[1,1,1,1],"uin":"1152921504741490647"}}}""",
            QqApi.vkeyRequestBody(songs, token()),
        )
    }

    @Test
    fun `vkey request body rejects malformed song id or unknown quality`() {
        assertNull(QqApi.vkeyRequestBody(listOf(song("onlymid", Quality.SQ)), null))
        assertNull(QqApi.vkeyRequestBody(listOf(song("a<>b", Quality.EQ)), null))
        assertNull(QqApi.vkeyRequestBody(listOf(SongBean(songId = "a<>b")), null))
    }

    @Test
    fun `search url`() {
        assertEquals(
            "https://u.y.qq.com/cgi-bin/musicu.fcg?data=%7B%22comm%22%3A%7B%22ct%22%3A%2219%22%2C%22cv%22%3A%221873%22%2C%22wid%22%3A%220%22%7D%2C%22data%22%3A%7B%22method%22%3A%22DoSearchForQQMusicDesktop%22%2C%22module%22%3A%22music.search.SearchCgiService%22%2C%22param%22%3A%7B%22grp%22%3A1%2C%22num_per_page%22%3A20%2C%22page_num%22%3A2%2C%22query%22%3A%22%E5%91%A8%E6%9D%B0%E4%BC%A6+%E6%99%B4%E5%A4%A9%26%22x%22%22%2C%22search_type%22%3A0%7D%7D%7D",
            QqApi.searchUrl("周杰伦 晴天&\"x\"", 20, 1),
        )
    }

    @Test
    fun `song detail and lyric urls`() {
        val songMid = QqApi.splitSongId("003OUlho2HcRHC<>abc")[0]
        assertEquals(
            "https://u.y.qq.com/cgi-bin/musicu.fcg?data=%7B%22songinfo%22%3A%7B%22module%22%3A%22music.pf_song_detail_svr%22%2C%22method%22%3A%22get_song_detail%22%2C%22param%22%3A%7B%22song_mid%22%3A%22003OUlho2HcRHC%22%7D%7D%7D",
            QqApi.songDetailUrl(songMid),
        )
        assertEquals(
            "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?g_tk=5381&format=json&songmid=003OUlho2HcRHC",
            QqApi.lyricUrl(songMid),
        )
    }

    @Test
    fun `login request body`() {
        assertEquals(
            """{"comm":{"tmeLoginType":1,"wid":"0"},"data":{"method":"Login","module":"music.login.LoginServer","param":{"access_token":"","musickey":"Q_H_L_5abc<def>=&x==","expired_at":1957831066,"openid":"opCFJw8b_qxl5vdj","refresh_token":"59_qz4-IC7oLI7GSx","str_musicid":"1152921504741490647","unionid":"oqFLxslbFUZPalHg"}}}""",
            QqApi.loginRequestBody(token()),
        )
    }

    @Test
    fun `login token json round trip matches legacy`() {
        assertEquals(
            """{"openid":"opCFJw8b_qxl5vdj","refresh_token":"59_qz4-IC7oLI7GSx","access_token":"","expired_at":1957831066,"str_musicid":"1152921504741490647","musickey":"Q_H_L_5abc<def>=&x==","musickeyCreateTime":1700000000,"unionid":"oqFLxslbFUZPalHg"}""",
            GsonUtil.toJson(token()),
        )

        val loginResp = """{"code":0,"data":{"code":0,"data":{"openid":"o1","refresh_token":"r1","access_token":"a1","expired_at":1957831066,"musicid":123,"str_musicid":"115","musickey":"Q_H=L","musickeyCreateTime":1700000000,"unionid":"u1","extra":"x"}}}"""
        assertEquals(
            """{"openid":"o1","refresh_token":"r1","access_token":"a1","expired_at":1957831066,"str_musicid":"115","musickey":"Q_H=L","musickeyCreateTime":1700000000,"unionid":"u1"}""",
            GsonUtil.toJson(GsonUtil.fromJson<QqLoginResponse>(loginResp)?.data?.token),
        )

        // 响应里没有 data 时，旧版会写回一个空 token
        assertEquals(
            """{"expired_at":0,"musickeyCreateTime":0}""",
            GsonUtil.toJson(GsonUtil.fromJson<QqLoginResponse>("""{"code":1000}""")?.data?.token),
        )
    }
}
