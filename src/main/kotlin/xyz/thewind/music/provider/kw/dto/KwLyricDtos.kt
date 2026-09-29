package xyz.thewind.music.provider.kw.dto

/** m.kuwo.cn/newh5/singles/songinfoandlrc 的响应 */
class KwLyricResponse {
    var data: KwLyricData? = KwLyricData()
}

class KwLyricData {
    var lrclist: List<KwLyricLine>? = null
}

class KwLyricLine {
    var lineLyric: String? = null

    /** 秒数，形如 `65.123` */
    var time: String? = null
}
