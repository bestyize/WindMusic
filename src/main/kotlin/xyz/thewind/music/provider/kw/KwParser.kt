package xyz.thewind.music.provider.kw

import org.slf4j.LoggerFactory
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.kw.dto.KwLyricResponse
import xyz.thewind.music.provider.kw.dto.KwPlayUrlResponse
import xyz.thewind.music.provider.kw.dto.KwSearchResponse
import xyz.thewind.music.provider.kw.dto.KwSongInfoResponse

/**
 * 把酷我各接口的 JSON 响应转换成 [SongBean] / 歌词文本。纯函数，不发请求。
 */
internal object KwParser {
    private val log = LoggerFactory.getLogger(KwParser::class.java)

    fun parseSearch(response: String): List<SongBean> {
        val resp = GsonUtil.fromJson(response, KwSearchResponse::class.java) ?: return emptyList()
        return resp.songList.orEmpty().map { detail ->
            SongBean(
                songSrc = MusicSource.KW,
                songId = detail.songId,
                songName = detail.songName,
                singersId = detail.singersId,
                singersName = detail.singersName,
                albumId = detail.albumId,
                albumName = detail.albumName,
                quality = detail.quality,
                picUrl = detail.picUrl,
            )
        }
    }

    /** 返回播放地址；响应无法解析或不含 url 时返回 null */
    fun parsePlayUrl(response: String): String? =
        GsonUtil.fromJson(response, KwPlayUrlResponse::class.java)?.data?.url

    /** 转成 LRC 文本，每行 `[mm:ss.xx]歌词\n`。某一行时间格式异常时，保留之前已转换的行。 */
    fun parseLyric(response: String): String {
        val sb = StringBuilder()
        try {
            val lines = GsonUtil.fromJson(response, KwLyricResponse::class.java)?.data?.lrclist ?: return ""
            for (line in lines) {
                // lineLyric 为 null 时与旧版一样输出字面量 "null"
                sb.append("[${convertToLyricTime(line.time)}]${line.lineLyric}\n")
            }
        } catch (e: Exception) {
            log.warn("parse kw lyric failed: {}", e.toString())
        }
        return sb.toString()
    }

    /**
     * 秒数（如 `65.123`）转 LRC 时间（`01:05.12`），小数只保留两位（截断，不四舍五入）。
     * 格式不合法（为空、没有小数点、小数不足两位）返回空串；整数部分不是数字时抛 [NumberFormatException]。
     */
    fun convertToLyricTime(time: String?): String {
        if (time == null || time.length < 4 || !time.contains(".") || time.lastIndexOf(".") + 2 > time.length - 1) {
            return ""
        }
        val truncated = time.substring(0, time.indexOf(".") + 3)
        val len = truncated.length
        val subSecond = truncated.substring(len - 2, len)
        val timeInSecond = truncated.substring(0, len - 3).toInt()
        val second = timeInSecond % 60
        val minute = timeInSecond / 60
        val sb = StringBuilder()
        if (minute < 10) sb.append("0")
        sb.append(minute).append(":")
        if (second < 10) sb.append("0")
        sb.append(second).append(".").append(subSecond)
        return sb.toString()
    }

    /** 响应无法解析或没有 data 时返回 null */
    fun parseSongDetail(songId: String, response: String): SongBean? {
        val data = GsonUtil.fromJson(response, KwSongInfoResponse::class.java)?.data ?: return null
        return SongBean(
            songSrc = MusicSource.KW,
            songId = songId,
            songName = data.songName,
            singersId = listOf(data.singerId.toString()),
            picUrl = data.pic,
            albumName = data.albumName,
            quality = if (data.hasLossless) Quality.SQ else Quality.HQ,
            albumId = data.albumId.toString(),
            singersName = listOfNotNull(data.singerName),
        )
    }
}
