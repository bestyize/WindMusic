package xyz.thewind.music.provider.wy.dto

import com.google.gson.annotations.SerializedName

/** `/api/cloudsearch/get/web` 的响应。 */
class SearchResponse {
    var result: SearchResult? = SearchResult()
}

class SearchResult {
    var songs: List<SearchSong?>? = null
}

class SearchSong {
    var id: Long = 0
    var name: String? = null

    @SerializedName("ar")
    var artists: List<Artist?>? = null

    @SerializedName("al")
    var album: Album? = null

    /** 平台返回的 MV id，没有 MV 时为 0 */
    @SerializedName("mv")
    var mvId: Long = 0

    /** Hi-Res 音质信息，存在即表示有 Hi-Res 版本 */
    @SerializedName("hr")
    var hiRes: QualityInfo? = null

    var privilege: Privilege? = null
}

/** 歌手，搜索接口（`ar`）和详情接口（`artists`）共用。 */
class Artist {
    var id: Long = 0
    var name: String? = null
}

/** 专辑，搜索接口（`al`）和详情接口（`album`）共用。 */
class Album {
    var id: Long = 0
    var name: String? = null
    var picUrl: String? = null
}

class QualityInfo {
    @SerializedName("br")
    var bitRate: String? = null

    @SerializedName("size")
    var fileSize: Long = 0
}

class Privilege {
    /** 当前账号可获取的最高码率 */
    var maxbr: Int = 0
}
