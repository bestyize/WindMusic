package xyz.thewind.music.model

/** 音乐平台代号，也是接口参数 `src` 的取值。 */
object MusicSource {
    const val QQ = "QQ"
    const val WY = "WY"
    const val MG = "MG"
    const val KW = "KW"
    const val KG = "KG"
}

/** 音质代号，从高到低。 */
object Quality {
    /** 杜比全景声 / Z3D，仅咪咕 */
    const val EQ = "EQ"
    /** Hi-Res 24bit FLAC */
    const val ZQ = "ZQ"
    /** 无损 FLAC */
    const val SQ = "SQ"
    /** 320k MP3 */
    const val HQ = "HQ"
    /** 192k/128k MP3 */
    const val PQ = "PQ"
    /** 最低音质 */
    const val LQ = "LQ"
}

const val USER_AGENT_ANDROID =
    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Mobile Safari/537.36"
