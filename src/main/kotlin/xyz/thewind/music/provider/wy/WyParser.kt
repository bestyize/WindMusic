package xyz.thewind.music.provider.wy

import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.model.MusicSource
import xyz.thewind.music.model.Quality
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.wy.dto.LyricResponse
import xyz.thewind.music.provider.wy.dto.PlayUrlResponse
import xyz.thewind.music.provider.wy.dto.SearchResponse
import xyz.thewind.music.provider.wy.dto.SearchSong
import xyz.thewind.music.provider.wy.dto.SongDetailResponse

/** 把网易云接口的 JSON 响应转换成 [SongBean] 等结果，解析失败返回空结果。 */
internal object WyParser {

    fun parseSearchResult(json: String): List<SongBean> {
        val songs = GsonUtil.fromJson(json, SearchResponse::class.java)?.result?.songs ?: return emptyList()
        val result = ArrayList<SongBean>()
        for (song in songs) {
            // 与旧版一致：某首歌缺少 ar/al/privilege 时停止解析，只返回之前已解析的歌曲
            result += song?.toSongBean() ?: break
        }
        return result
    }

    /** 播放链接，取不到返回 null。 */
    fun parsePlayUrl(json: String): String? =
        GsonUtil.fromJson(json, PlayUrlResponse::class.java)?.data?.firstOrNull()?.url

    fun parseLyric(json: String): String =
        GsonUtil.fromJson(json, LyricResponse::class.java)?.lrc?.get("lyric")?.toString() ?: ""

    /** 没有歌曲、没有歌手或专辑缺少封面时返回 null（与旧版一致）。 */
    fun parseSongDetail(songId: String, json: String): SongBean? {
        val song = GsonUtil.fromJson(json, SongDetailResponse::class.java)?.songs?.firstOrNull() ?: return null
        val artists = song.artists?.map { it ?: return null }?.takeIf { it.isNotEmpty() } ?: return null
        val bean = SongBean(songSrc = MusicSource.WY, songId = songId, songName = song.name)
        song.album?.let { album ->
            val picUrl = album.picUrl ?: return null
            bean.picUrl = picUrl.replace("http://", "https://")
            bean.albumId = album.id.toString()
            bean.albumName = album.name
        }
        bean.singersId = artists.map { it.id.toString() }
        bean.singersName = artists.map { it.name.orEmpty() }
        return bean
    }

    private fun SearchSong.toSongBean(): SongBean? {
        val artists = artists?.map { it ?: return null } ?: return null
        val album = album ?: return null
        val privilege = privilege ?: return null
        return SongBean(
            songSrc = MusicSource.WY,
            songId = id.toString(),
            songName = name,
            singersId = artists.map { it.id.toString() },
            singersName = artists.map { it.name.orEmpty() },
            albumId = album.id.toString(),
            albumName = album.name,
            // 搜索结果的封面保持原样，不做 http -> https 替换（与旧版一致）
            picUrl = album.picUrl,
            quality = when {
                privilege.maxbr < 999000 -> Quality.HQ
                hiRes != null -> Quality.ZQ
                else -> Quality.SQ
            },
            mvUrl = mvId.toString(),
        )
    }
}
