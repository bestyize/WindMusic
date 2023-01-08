package xyz.thewind.music.music.impl.mg.bean.search

import com.google.gson.annotations.SerializedName
import xyz.thewind.music.music.common.MusicCommonConfig

class RateFormat {
    var resourceType: String? = null

    @SerializedName("formatType")
    var quality: String? = null
        get() {
            return if ("z3d" == field) {
                MusicCommonConfig.QUALITY_EQ
            } else field
        }
    var size: String? = null

    @SerializedName(value = "fileType", alternate = ["androidFileType"])
    var fileType: String? = null
}