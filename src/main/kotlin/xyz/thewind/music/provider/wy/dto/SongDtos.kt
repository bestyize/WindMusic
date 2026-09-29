package xyz.thewind.music.provider.wy.dto

/** `/api/song/detail` 的响应。 */
class SongDetailResponse {
    var songs: List<DetailSong?>? = ArrayList()
}

class DetailSong {
    var name: String? = null
    var artists: List<Artist?>? = ArrayList()
    var album: Album? = null
}

/** `/api/song/enhance/player/url` 的响应。 */
class PlayUrlResponse {
    var data: List<PlayUrl?>? = ArrayList()
}

class PlayUrl {
    var url: String? = null
}

/** `/api/song/lyric` 的响应，歌词在 `lrc.lyric`。 */
class LyricResponse {
    var lrc: Map<String, Any?>? = HashMap()
}
