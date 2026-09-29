package xyz.thewind.music.provider.kw.dto

import com.google.gson.annotations.SerializedName

/** nmobi.kuwo.cn/mobi.s?type=convert_url_with_sign 的响应 */
class KwPlayUrlResponse {
    var code: Int = 0
    var data: KwPlayUrlData? = KwPlayUrlData()
}

class KwPlayUrlData {
    @SerializedName("bitrate")
    var bitRate: Int = 0
    var format: String? = null
    var url: String? = null
}
