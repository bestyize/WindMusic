package xyz.thewind.music.music.impl.mg.bean.search

import com.google.gson.annotations.SerializedName
import xyz.thewind.music.music.common.MusicCommonConfig
import java.util.*

class SongDetail {
    var id: String? = null
    var contentId: String? = null

    @JvmField
    @SerializedName("copyrightId")
    var songId: String? = null

    @JvmField
    @SerializedName("name")
    var songName: String? = null

    @SerializedName(value = "singerList", alternate = ["singers"])
    var singers: List<Singer> = ArrayList()
    var albums: List<Album> = ArrayList()
    var album: String? = null
    var albumId: String? = null
        get() {
            if ((field?.length ?: 0) > 0) {
                return field
            }
            return if (albums.isEmpty()) {
                ""
            } else albums[0].id
        }
    private var lrcUrl: String? = null

    @SerializedName(value = "lyricUrl")
    var lyricUrl: String? = null
        get() {
            return if (lrcUrl != null && lrcUrl!!.isNotEmpty()) {
                lrcUrl
            } else field
        }
    private var img3: String? = null
    var mvId: String? = null
        get() {
            if (field?.isNotEmpty() == true) {
                return field
            }
            return if (mvList.isEmpty()) {
                field
            } else mvList[0].mvId
        }
    private var mvList: List<Mv> = ArrayList()
    private var imgItems: List<ImgItem>? = null
    private var newRateFormats: List<RateFormat> = ArrayList()

    @SerializedName("z3dCode")
    var eqRateFormat: RateFormat? = null
    private var audioFormats: List<AudioFormat> = ArrayList()

    val singersId: List<String?>
        get() {
            val singerIdList: MutableList<String?> = LinkedList()
            for (singer in singers) {
                singerIdList.add(singer.id)
            }
            return singerIdList
        }
    val singersName: List<String?>
        get() {
            val singerNameList: MutableList<String?> = LinkedList()
            for (singer in singers) {
                singerNameList.add(singer.name)
            }
            return singerNameList
        }

    val albumName: String?
        get() {
            if (album != null && album!!.length > 0) {
                return album
            }
            return if (albums.isEmpty()) {
                ""
            } else albums[0].name
        }


    val quality: String
        get() {
            var size = newRateFormats.size
            if (audioFormats.isNotEmpty()) {
                size = audioFormats.size
            }
            if (eqRateFormat != null || size >= 5) {
                return MusicCommonConfig.QUALITY_EQ
            }
            if (size >= 4) {
                return MusicCommonConfig.QUALITY_ZQ
            }
            if (size == 3) {
                return MusicCommonConfig.QUALITY_SQ
            }
            return if (size == 2) {
                MusicCommonConfig.QUALITY_HQ
            } else MusicCommonConfig.QUALITY_PQ
        }
    val picUrl: String? = null
        get() {
            if (img3?.isNotEmpty() == true) {
                return "https://d.musicapp.migu.cn/$img3"
            }
            return if (imgItems?.isEmpty() == true) {
                field
            } else imgItems!![0].imgUrl
        }
}