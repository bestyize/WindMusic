package xyz.thewind.music.provider.qq

import com.google.gson.Gson
import xyz.thewind.music.common.HttpUtil
import xyz.thewind.music.common.javaSplit
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.model.USER_AGENT_ANDROID
import xyz.thewind.music.provider.qq.dto.QqLoginToken
import xyz.thewind.music.provider.qq.dto.VkeyRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * QQ 音乐接口的请求构造与发送，只返回原始响应文本，解析交给 [QqMusicProvider]。
 *
 * 除歌词外的接口都走 `musicu.fcg`：GET 时请求体 JSON 经 URL 编码放在 `data` 参数里，POST 时直接作为请求体。
 */
internal object QqApi {
    private const val MUSICU_API = "https://u.y.qq.com/cgi-bin/musicu.fcg"
    private const val LYRIC_API = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg"
    private const val ALBUM_PIC_PREFIX = "https://y.gtimg.cn/music/photo_new/T002R800x800M000"

    /** vkey 接口返回的 purl 需要拼在这个域名后面 */
    const val DOWNLOAD_SERVER = "https://isure.stream.qqmusic.qq.com/"

    /** songId 格式为 `songMid<>mediaMid`：歌词、详情只用 songMid，下载文件名需要 mediaMid */
    private const val SONG_ID_SEPARATOR = "<>"

    /** 音质 -> 下载文件名模板，`songid` 会被替换成 mediaMid（filename）或 songMid（musicfile） */
    private val fileNameTemplates: HashMap<String, String> = hashMapOf(
        Quality.ZQ to "RS01songid.flac",
        Quality.SQ to "F000songid.flac",
        Quality.HQ to "M800songid.mp3",
        Quality.PQ to "M500songid.mp3",
        Quality.LQ to "C400songid.m4a",
    )

    private val vkeyHeaders: HashMap<String, String> = hashMapOf(
        "User-Agent" to "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
        "Host" to "u.y.qq.com",
        "Connection" to "Keep-Alive",
        "Accept" to "application/json",
    )

    // 旧版这里用的是 new Gson()（开启 HTML 转义，= < > 等会被转成 =），与 GsonUtil 不同，保持一致
    private val htmlEscapingGson = Gson()

    fun splitSongId(songId: String): Array<String> = songId.javaSplit(SONG_ID_SEPARATOR)

    fun albumPicUrl(albumMid: String?): String = "$ALBUM_PIC_PREFIX${albumMid}.jpg"

    // ---------------------------------------------------------------- 搜索

    fun searchUrl(keyword: String, num: Int, page: Int): String {
        // keyword 原样拼进 JSON（不转义），与旧版一致；page 从 0 开始，接口从 1 开始
        val data = """{"comm":{"ct":"19","cv":"1873","wid":"0"},"data":{"method":"DoSearchForQQMusicDesktop",""" +
            """"module":"music.search.SearchCgiService","param":{"grp":1,"num_per_page":$num,"page_num":${page + 1},""" +
            """"query":"$keyword","search_type":0}}}"""
        return "$MUSICU_API?data=" + URLEncoder.encode(data, StandardCharsets.UTF_8)
    }

    fun search(keyword: String, num: Int, page: Int): String =
        HttpUtil.get(searchUrl(keyword, num, page), hashMapOf("User-Agent" to USER_AGENT_ANDROID))

    // ---------------------------------------------------------------- 下载链接

    /**
     * 构造批量获取下载链接的请求体，每首歌按自身的 quality 请求。
     * 任一歌曲的 songId 不是 `mid<>mediaMid` 格式或音质未知时返回 null（旧版在此抛异常）。
     */
    fun vkeyRequestBody(songs: List<SongBean>, token: QqLoginToken?): String? {
        val request = VkeyRequest()
        if (token != null) {
            request.comm.authst = token.musicKey
            request.comm.uin = token.musicId
            request.queryvkey.param.uin = token.musicId
        }
        val filenames = ArrayList<String>()
        val musicfiles = ArrayList<String>()
        val songmids = ArrayList<String>()
        val songtypes = ArrayList<Int>()
        for (song in songs) {
            val template = fileNameTemplates[song.quality] ?: return null
            val parts = splitSongId(song.songId ?: return null)
            val songMid = parts.getOrNull(0) ?: return null
            val mediaMid = parts.getOrNull(1) ?: return null
            filenames += template.replace("songid", mediaMid)
            musicfiles += template.replace("songid", songMid)
            songmids += songMid
            songtypes += 1
        }
        request.queryvkey.param.apply {
            filename = filenames
            musicfile = musicfiles
            songmid = songmids
            songtype = songtypes
        }
        return htmlEscapingGson.toJson(request)
    }

    fun queryVkey(requestBody: String): String = HttpUtil.post(MUSICU_API, requestBody, vkeyHeaders)

    // ---------------------------------------------------------------- 歌词 / 详情

    fun lyricUrl(songMid: String): String = "$LYRIC_API?g_tk=5381&format=json&songmid=$songMid"

    fun lyric(songMid: String): String =
        HttpUtil.get(lyricUrl(songMid), hashMapOf("Referer" to "https://y.qq.com/portal/player.html"))

    fun songDetailUrl(songMid: String): String {
        val data = """{"songinfo":{"module":"music.pf_song_detail_svr","method":"get_song_detail",""" +
            """"param":{"song_mid":"$songMid"}}}"""
        return "$MUSICU_API?data=" + URLEncoder.encode(data, StandardCharsets.UTF_8)
    }

    fun songDetail(songMid: String): String = HttpUtil.get(songDetailUrl(songMid))

    // ---------------------------------------------------------------- 登录

    /** 用旧 token 换新 token 的请求体。占位符按固定顺序依次替换，与旧版一致。 */
    fun loginRequestBody(token: QqLoginToken): String {
        val template = """{"comm":{"tmeLoginType":1,"wid":"0"},"data":{"method":"Login","module":"music.login.LoginServer",""" +
            """"param":{"access_token":"ACCESS_TOKEN","musickey":"MUSIC_KEY","expired_at":1957831066,"openid":"OPENID",""" +
            """"refresh_token":"REFRESH_TOKEN","str_musicid":"MUSICID","unionid":"UNIONID"}}}"""
        // 旧版字段为 null 时 String.replace 会抛 NPE，这里按空串处理
        return template
            .replace("ACCESS_TOKEN", token.accessToken.orEmpty())
            .replace("MUSIC_KEY", token.musicKey.orEmpty())
            .replace("OPENID", token.openid.orEmpty())
            .replace("REFRESH_TOKEN", token.refreshToken.orEmpty())
            .replace("MUSICID", token.musicId.orEmpty())
            .replace("UNIONID", token.unionid.orEmpty())
    }

    // 旧版地址末尾带一个 "?"
    fun login(token: QqLoginToken): String = HttpUtil.post("$MUSICU_API?", loginRequestBody(token))
}
