package xyz.thewind.music.provider.qq.dto

import com.google.gson.annotations.SerializedName
import xyz.thewind.music.provider.qq.QqApi

/**
 * `vkey.GetVkeyServer.CgiGetVkey` 的请求体，由 Gson 序列化后 POST 到 musicu.fcg。
 *
 * 字段集合、名称、默认值与声明顺序（即 JSON 键顺序）必须与旧版 DownloadRequestBean 完全一致，
 * 值为 null 的字段不会出现在请求体里。
 */
class VkeyRequest {
    var comm = Comm()
    var queryvkey = QueryVkey()

    class Comm {
        /** 登录后的 musickey */
        var authst: String? = null
        var tmeAppID = "qqmusic"
        var tmeLoginType = 1
        /** 登录后的 str_musicid */
        var uin: String? = null
        var wid = "0"
        var ct = "19"
        var cv = "1873"
    }

    class QueryVkey {
        var method = "CgiGetVkey"
        var module = "vkey.GetVkeyServer"
        var param = Param()
    }

    class Param {
        var checklimit = 0
        var ctx = 1
        var downloadfrom = 0
        /** 形如 `F000{mediaMid}.flac` */
        var filename: List<String>? = null
        var guid = "810BA5E07103080839C18EE1FD2EE9B5"
        /** 形如 `F000{songMid}.flac` */
        var musicfile: List<String>? = null
        var nettype = ""
        var referer = "y.qq.com"
        var scene = 0
        var songmid: List<String>? = null
        var songtype: List<Int>? = null
        var uin: String? = null
    }
}

/** 响应：`{"code":0,"queryvkey":{"code":0,"data":{"midurlinfo":[{"purl":"...","qmdlfromtag":"..."}]}}}` */
class VkeyResponse {
    var queryvkey: Data? = null

    class Data {
        var data: Info? = null
    }

    class Info {
        /** 与请求中的歌曲一一对应、顺序一致 */
        var midurlinfo: List<MidUrlInfo>? = null
    }
}

class MidUrlInfo {
    var purl: String? = null
    var qmdlfromtag: String? = null

    /** 完整下载链接。purl 为空（无版权或无权限）时也照常拼接，与旧版一致。 */
    val downloadLink: String
        get() = QqApi.DOWNLOAD_SERVER + purl + "&fromtag=" + qmdlfromtag
}
