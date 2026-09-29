package xyz.thewind.music.provider.kg.dto

import com.google.gson.annotations.SerializedName
import xyz.thewind.music.model.Quality

/** complexsearch.kugou.com/v2/search/song 的响应 */
class SearchResponse {
    var data: SearchData? = SearchData()
}

class SearchData {
    var lists: List<SearchSong>? = ArrayList()
}

class SearchSong {
    @SerializedName("SongName")
    var songName: String? = null

    @SerializedName("AlbumName")
    var albumName: String? = null

    @SerializedName("AlbumID")
    var albumId: String? = null

    /** Hi-Res 音质 hash */
    @SerializedName("ResFileHash")
    var zqHash: String? = null

    @SerializedName("SQFileHash")
    var sqHash: String? = null

    @SerializedName("HQFileHash")
    var hqHash: String? = null

    /** 标准音质 hash */
    @SerializedName("FileHash")
    var pqHash: String? = null

    @SerializedName("Singers")
    var singers: List<SearchSinger>? = ArrayList()

    @SerializedName("mvdata")
    var mvs: List<SearchMv>? = ArrayList()

    /**
     * 酷狗的 songId 约定为 `PQ-HQ-SQ-ZQ` 四个 hash 用 `-` 拼接，缺失的 hash 为空串（或 "null"）。
     * 下载时按音质取对应位置的 hash。
     */
    val songId: String
        get() = "$pqHash-$hqHash-$sqHash-$zqHash"

    /** 有 hash 的最高音质 */
    val quality: String
        get() = when {
            !zqHash.isNullOrEmpty() -> Quality.ZQ
            !sqHash.isNullOrEmpty() -> Quality.SQ
            !hqHash.isNullOrEmpty() -> Quality.HQ
            else -> Quality.PQ
        }

    /** 第一个 MV 的 hash，没有 MV 时为空串 */
    val mvId: String?
        get() {
            val list = mvs.orEmpty()
            return if (list.isEmpty()) "" else list[0].mvId
        }

    val singersName: List<String>
        get() = singers.orEmpty().map { it.name.orEmpty() }

    val singersId: List<String>
        get() = singers.orEmpty().map { it.id.toString() }
}

class SearchSinger {
    var name: String? = null
    var id: Int = 0
}

class SearchMv {
    @SerializedName("hash")
    var mvId: String? = null
}
