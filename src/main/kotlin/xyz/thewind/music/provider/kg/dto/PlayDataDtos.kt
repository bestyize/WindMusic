package xyz.thewind.music.provider.kg.dto

import com.google.gson.annotations.SerializedName

/** wwwapi.kugou.com/yy/index.php?r=play/getdata 的响应，用于歌曲详情 */
class PlayDataResponse {
    var data: PlayData? = PlayData()
}

class PlayData {
    @SerializedName("audio_name")
    var songName: String? = null

    @SerializedName("img")
    var picUrl: String? = null

    @SerializedName("album_name")
    var albumName: String? = null

    @SerializedName("album_id")
    var albumId: String? = null

    var authors: List<PlayDataAuthor>? = null
}

class PlayDataAuthor {
    @SerializedName("author_id")
    var singerId: String? = null

    @SerializedName("author_name")
    var singerName: String? = null
}
