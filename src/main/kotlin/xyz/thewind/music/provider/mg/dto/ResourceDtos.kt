package xyz.thewind.music.provider.mg.dto

import com.google.gson.annotations.SerializedName

// 咪咕 resourceinfo.do（歌曲资源信息）和 cms_detail_tag（歌词）接口的响应结构。

class MgResourceResponse {
    var resource: List<MgResource?>? = ArrayList()
}

class MgResource {
    var songName: String? = null
    var singerId: String? = null

    @SerializedName("singer")
    var singerName: String? = null

    @SerializedName("album")
    var albumName: String? = null

    var albumImgs: List<MgAlbumImg?>? = ArrayList()
    var lrcUrl: String? = null

    /** 各音质的下载格式，formatType 即音质代号（PQ/HQ/SQ/ZQ） */
    @SerializedName("newRateFormats")
    var rateFormats: List<MgRateFormat?>? = null

    /** 杜比全景声（Z3D），对应 EQ 音质 */
    @SerializedName("z3dCode")
    var eqFormat: MgRateFormat? = null
}

class MgRateFormat {
    var formatType: String? = null

    @SerializedName(value = "url", alternate = ["androidUrl"])
    private var rawUrl: String? = null

    /** 接口给的是 ftp 地址，把 `/public` 之后的路径拼到 http 下载域名上；其它地址原样返回 */
    val url: String?
        get() {
            val raw = rawUrl ?: return null
            return if (raw.contains("/public")) "http://freetyst.nf.migu.cn" + raw.substring(raw.indexOf("/public")) else raw
        }
}

class MgAlbumImg {
    var img: String? = null
}

class MgLyricResponse {
    var data: Data? = Data()

    class Data {
        var lyricLrc: String? = null
    }
}
