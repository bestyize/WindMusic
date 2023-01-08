package xyz.thewind.music.music.impl.mg.bean

class LyricResponseBean {
    @JvmField
    var data = Data()

    class Data {
        @JvmField
        var lyricLrc: String? = null
    }
}