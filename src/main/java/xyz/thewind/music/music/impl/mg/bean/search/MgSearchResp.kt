package xyz.thewind.music.music.impl.mg.bean.search

import com.google.gson.annotations.SerializedName

class MgSearchResp {
    var code: String? = null
    var info: String? = null

    @SerializedName("songResultData")
    var songResultData = SongResultData()

    @SerializedName("tagSongResultData")
    var tagSongResultData: SongResultData = SongResultData()
    val result: SongResultData
        get() = if (tagSongResultData.resultList.size > 0) {
            tagSongResultData
        } else songResultData
}