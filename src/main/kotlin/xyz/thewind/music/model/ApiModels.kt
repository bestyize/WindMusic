package xyz.thewind.music.model

/** 管理类接口的通用响应，code = 0 表示成功。 */
data class CommonResp(
    val code: Int = -1,
    val msg: String? = null,
    val obj: Any? = null,
)

/** 分享播放页（/player）使用的歌曲信息，字段名与前端播放器约定一致。 */
data class PlayerBean(
    val title: String,
    val author: String,
    val pic: String,
    val url: String,
    val lyc: String,
)
