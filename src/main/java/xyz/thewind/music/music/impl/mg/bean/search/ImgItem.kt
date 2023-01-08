package xyz.thewind.music.music.impl.mg.bean.search

import com.google.gson.annotations.SerializedName

class ImgItem {
    var imageSizeTYpe: String? = null

    @JvmField
    @SerializedName("img")
    var imgUrl: String? = null
}