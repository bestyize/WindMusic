package xyz.thewind.music.provider.mg.dto

import com.google.gson.annotations.SerializedName
import xyz.thewind.music.model.Quality

// 咪咕 jadeite 搜索接口（searchAll）的响应结构。
// 字段一律可空：Gson 会把 JSON 里显式的 null 原样写进来，即使声明为非空类型。

class MgSearchResponse {
    var songResultData: MgSongResultData? = MgSongResultData()

    /** 标签歌曲结果，通常比普通歌曲结果更准 */
    var tagSongResultData: MgSongResultData? = MgSongResultData()

    /** 优先取标签歌曲结果，为空时回退到普通歌曲结果 */
    val result: MgSongResultData?
        get() = if (tagSongResultData?.resultList.orEmpty().isNotEmpty()) tagSongResultData else songResultData
}

class MgSongResultData {
    /** 每组是同一首歌的多个版本，只取第一个 */
    var resultList: List<List<MgSongDetail?>?>? = ArrayList()
}

/**
 * 搜索结果里的单曲。同一信息在不同版本的接口里可能出现在不同字段，
 * 所以原始字段是私有的，对外暴露按优先级取值后的属性。
 */
class MgSongDetail {
    @SerializedName("copyrightId")
    var songId: String? = null

    @SerializedName("name")
    var songName: String? = null

    @SerializedName(value = "singerList", alternate = ["singers"])
    var singers: List<MgSinger?>? = ArrayList()

    var albums: List<MgAlbum?>? = ArrayList()

    @SerializedName("album")
    private var rawAlbumName: String? = null

    @SerializedName("albumId")
    private var rawAlbumId: String? = null

    private var lrcUrl: String? = null

    @SerializedName("lyricUrl")
    private var rawLyricUrl: String? = null

    /** 封面的相对路径 */
    private var img3: String? = null

    private var imgItems: List<MgImgItem?>? = null

    @SerializedName("picUrl")
    private var rawPicUrl: String? = null

    @SerializedName("mvId")
    private var rawMvId: String? = null

    private var mvList: List<MgMv?>? = ArrayList()

    private var newRateFormats: List<MgFormat?>? = ArrayList()

    private var audioFormats: List<MgFormat?>? = ArrayList()

    /** 杜比全景声（Z3D）格式，存在即说明有 EQ 音质 */
    @SerializedName("z3dCode")
    private var z3dFormat: MgFormat? = null

    /** 歌手 id，跳过缺失的值 */
    val singersId: List<String>
        get() = singers.orEmpty().mapNotNull { it?.id }

    /** 歌手名，跳过缺失的值 */
    val singersName: List<String>
        get() = singers.orEmpty().mapNotNull { it?.name }

    /** 优先 albumId 字段，否则取 albums 第一项；albums 为空时是空串 */
    val albumId: String?
        get() {
            if (!rawAlbumId.isNullOrEmpty()) return rawAlbumId
            val list = albums.orEmpty()
            return if (list.isEmpty()) "" else list[0]?.id
        }

    /** 优先 album 字段，否则取 albums 第一项；albums 为空时是空串 */
    val albumName: String?
        get() {
            if (!rawAlbumName.isNullOrEmpty()) return rawAlbumName
            val list = albums.orEmpty()
            return if (list.isEmpty()) "" else list[0]?.name
        }

    /** 优先 lrcUrl 字段，否则 lyricUrl */
    val lyricUrl: String?
        get() = if (!lrcUrl.isNullOrEmpty()) lrcUrl else rawLyricUrl

    /** 优先 mvId 字段，否则取 mvList 第一项的 copyrightId */
    val mvId: String?
        get() {
            if (!rawMvId.isNullOrEmpty()) return rawMvId
            val list = mvList.orEmpty()
            return if (list.isEmpty()) rawMvId else list[0]?.mvId
        }

    /** 优先 img3（拼成完整地址），否则取 imgItems 第一项，都没有时用 picUrl 字段 */
    val picUrl: String?
        get() {
            if (!img3.isNullOrEmpty()) return "https://d.musicapp.migu.cn/$img3"
            val items = imgItems.orEmpty()
            return if (items.isEmpty()) rawPicUrl else items[0]?.imgUrl
        }

    /**
     * 可用的最高音质，按格式数量推断：有 Z3D 或 ≥5 种为 EQ，4 种 ZQ，3 种 SQ，2 种 HQ，其余 PQ。
     * audioFormats 非空时以它为准，否则看 newRateFormats。
     */
    val quality: String
        get() {
            val audio = audioFormats.orEmpty()
            val size = if (audio.isNotEmpty()) audio.size else newRateFormats.orEmpty().size
            return when {
                z3dFormat != null || size >= 5 -> Quality.EQ
                size >= 4 -> Quality.ZQ
                size == 3 -> Quality.SQ
                size == 2 -> Quality.HQ
                else -> Quality.PQ
            }
        }
}

class MgSinger {
    var id: String? = null
    var name: String? = null
}

class MgAlbum {
    var id: String? = null
    var name: String? = null
}

class MgMv {
    @SerializedName("copyrightId")
    var mvId: String? = null
}

class MgImgItem {
    @SerializedName("img")
    var imgUrl: String? = null
}

/** 搜索结果里的音质格式，只用来统计数量 */
class MgFormat {
    var formatType: String? = null
    var size: String? = null
}
