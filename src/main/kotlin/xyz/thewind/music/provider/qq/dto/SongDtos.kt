package xyz.thewind.music.provider.qq.dto

import com.google.gson.annotations.SerializedName
import xyz.thewind.music.model.Quality
import xyz.thewind.music.provider.qq.QqApi

/** 搜索接口 `DoSearchForQQMusicDesktop` 的响应：`{"code":0,"data":{"data":{"body":{"song":{"list":[...]}}}}}` */
class QqSearchResponse {
    var code: Int = 0
    var data: Data? = null

    class Data {
        var data: Result? = null
    }

    class Result {
        var body: Body? = null
    }

    class Body {
        var song: SongList? = null
    }

    class SongList {
        var list: List<QqTrack>? = null
    }
}

/** 歌曲详情接口 `get_song_detail` 的响应：`{"songinfo":{"data":{"track_info":{...},"info":[...]}}}` */
class QqSongDetailResponse {
    var songinfo: SongInfo? = null

    class SongInfo {
        var data: Data? = null
    }

    class Data {
        @SerializedName("track_info")
        var trackInfo: QqTrack? = null

        /** 附加信息（歌词、发行时间等），按 type 区分 */
        var info: List<Info>? = null
    }

    class Info {
        var type: String? = null
        var content: List<Content>? = null
    }

    class Content {
        var value: String? = null
    }
}

/** 歌词接口响应，lyric 为 Base64 编码的 LRC 文本。 */
class QqLyricResponse {
    var lyric: String? = null
}

/** 搜索结果与歌曲详情共用的歌曲结构。 */
class QqTrack {
    var mid: String? = null
    var name: String? = null
    var album: QqAlbum? = null
    var file: QqSongFile? = null
    var mv: QqMv? = null

    @SerializedName("singer")
    var singers: List<QqSinger>? = null

    val singersName: List<String> get() = singers.orEmpty().map { it.name.orEmpty() }

    val singersId: List<String> get() = singers.orEmpty().map { it.mid.orEmpty() }

    /** 有 Hi-Res 文件为 ZQ，有 FLAC 为 SQ，否则按 HQ 处理。 */
    val quality: String
        get() = when {
            (file?.zqSize ?: 0L) > 0 -> Quality.ZQ
            (file?.sqSize ?: 0L) > 0 -> Quality.SQ
            else -> Quality.HQ
        }

    val picUrl: String get() = QqApi.albumPicUrl(album?.mid)
}

class QqAlbum {
    var mid: String? = null
    var name: String? = null
}

class QqSinger {
    var mid: String? = null
    var name: String? = null
}

class QqMv {
    var vid: String? = null
}

class QqSongFile {
    /** 下载文件名用的 mid，与歌曲 mid 不同 */
    @SerializedName("media_mid")
    var mediaMid: String? = null

    @SerializedName("size_flac")
    var sqSize: Long = 0

    @SerializedName("size_hires")
    var zqSize: Long = 0
}
