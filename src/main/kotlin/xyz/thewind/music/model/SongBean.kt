package xyz.thewind.music.model

import com.google.gson.annotations.SerializedName

/**
 * 统一的歌曲信息，搜索、下载、详情接口都返回它。
 *
 * [songId] 的格式由各平台自行约定（例如酷狗是多个音质 hash 用 `-` 拼接，QQ 是 `mid<>mediaMid`），
 * 前端只负责原样回传。
 */
class SongBean(
    var songSrc: String? = null,
    var songId: String? = null,
    var songName: String? = null,
    var singersId: List<String>? = null,
    var singersName: List<String>? = null,
    var albumId: String? = null,
    var albumName: String? = null,
    var picUrl: String? = null,
    var lrcUrl: String? = null,
    var mvUrl: String? = null,
    var songLink: String? = null,
    /** 该歌曲可用的最高音质，见 [Quality] */
    var quality: String? = null,
    downloadLinkMap: MutableMap<String, String>? = null,
) {
    // 批量下载时 SongBean 由 Gson 从客户端 JSON 解析，字段可能被显式置为 null，所以用可空的私有字段兜底
    @SerializedName("downloadLinkMap")
    private var links: MutableMap<String, String>? = downloadLinkMap

    /** 音质 -> 下载链接 */
    var downloadLinkMap: MutableMap<String, String>
        get() = links ?: HashMap<String, String>().also { links = it }
        set(value) {
            links = value
        }

    override fun toString(): String =
        "SongBean(songSrc=$songSrc, songId=$songId, songName=$songName, singersName=$singersName, " +
            "albumName=$albumName, quality=$quality, downloadLinkMap=$links)"
}
