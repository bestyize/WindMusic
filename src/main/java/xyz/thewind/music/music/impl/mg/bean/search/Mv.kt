package xyz.thewind.music.music.impl.mg.bean.search

import com.google.gson.annotations.SerializedName

class Mv {
    var id: String? = null

    @JvmField
    @SerializedName("copyrightId")
    var mvId: String? = null
}