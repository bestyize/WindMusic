package xyz.thewind.music.provider.kw.dto

import com.google.gson.annotations.SerializedName
import xyz.thewind.music.common.javaSplit
import xyz.thewind.music.model.Quality

/** search.kuwo.cn/r.s 搜索接口的响应 */
class KwSearchResponse {
    @SerializedName("abslist")
    var songList: List<KwSearchSong>? = ArrayList()
}

class KwSearchSong {
    @SerializedName("DC_TARGETID")
    var songId: String? = null

    @SerializedName("SONGNAME")
    var songName: String? = null

    @SerializedName("ALBUMID")
    var albumId: String? = null

    @SerializedName("ALBUM")
    var albumName: String? = null

    /** 多个歌手用 `&` 连接 */
    @SerializedName("ARTIST")
    var singerName: String? = null

    /** 多个歌手 ID 用 `&` 连接 */
    @SerializedName("ARTISTID")
    var singerId: String? = null

    @SerializedName("hts_MVPIC")
    var mvPic: String? = null

    /** 专辑封面的相对路径，例如 `120/s4s99/74/3451932382.jpg` */
    @SerializedName("web_albumpic_short")
    var albumPicPath: String? = null

    /** 优先用方形的专辑封面（路径开头的尺寸换成 300，分享页大图也清晰），没有时用 MV 截图 */
    val picUrl: String?
        get() = albumPicPath?.takeIf { it.isNotEmpty() }
            ?.let { "https://img1.kuwo.cn/star/albumcover/" + it.replaceFirst(Regex("^\\d+/"), "300/") }
            ?: mvPic?.takeIf { it.isNotEmpty() }

    /**
     * 可用音质列表，例如
     * `level:hr,bitrate:4000,format:flac,size:53.12Mb;level:ff,bitrate:2000,format:flac,size:27.29Mb;level:p,...`
     */
    @SerializedName("N_MINFO")
    var minInfo: String? = null

    val singersId: List<String>
        get() = singerId?.javaSplit("&")?.toList() ?: emptyList()

    val singersName: List<String>
        get() = singerName?.javaSplit("&")?.toList() ?: emptyList()

    /**
     * 最高可下载音质：取所有 `level` 中能映射到的最高一档。
     * 酷我还会列出 bcms、zply、zpga 等加密格式（mgg/mflac），下载接口拿不到，忽略。
     */
    val quality: String
        get() = levels().mapNotNull { LEVEL_TO_QUALITY[it] }.maxByOrNull { QUALITY_RANK.indexOf(it) } ?: Quality.LQ

    private fun levels(): List<String> =
        minInfo.orEmpty().javaSplit(";").flatMap { entry ->
            entry.javaSplit(",").mapNotNull { pair ->
                pair.javaSplit(":").takeIf { it.size == 2 && it[0] == "level" }?.get(1)
            }
        }

    private companion object {
        val LEVEL_TO_QUALITY = mapOf("hr" to Quality.ZQ, "ff" to Quality.SQ, "p" to Quality.HQ, "h" to Quality.PQ, "s" to Quality.LQ)
        val QUALITY_RANK = listOf(Quality.LQ, Quality.PQ, Quality.HQ, Quality.SQ, Quality.ZQ)
    }
}
