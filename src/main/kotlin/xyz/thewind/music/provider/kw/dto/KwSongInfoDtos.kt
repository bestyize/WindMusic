package xyz.thewind.music.provider.kw.dto

import com.google.gson.annotations.SerializedName

/** www.kuwo.cn/api/www/music/musicInfo 的响应 */
class KwSongInfoResponse {
    var code: Int = 0
    var data: KwSongInfo? = null
}

class KwSongInfo {
    @SerializedName("albumid")
    var albumId: Long = 0
    var hasLossless: Boolean = false

    @SerializedName("artist")
    var singerName: String? = null

    /** 封面地址，统一转成 https */
    var pic: String? = null
        get() = field?.replace("http://", "https://")

    @SerializedName("songId")
    var rid: Long = 0
    var duration: Int = 0

    @SerializedName("artistid")
    var singerId: Long = 0

    @SerializedName("album")
    var albumName: String? = null

    @SerializedName("name")
    var songName: String? = null
}
