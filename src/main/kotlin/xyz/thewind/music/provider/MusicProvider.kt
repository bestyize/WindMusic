package xyz.thewind.music.provider

import xyz.thewind.music.model.SongBean

/**
 * 一个音乐平台的接入实现。每个平台一个 Spring Bean，由 [xyz.thewind.music.service.MusicService] 按 [source] 分发。
 *
 * 实现约定：网络或解析失败时不抛异常，返回空列表 / 空串 / 原样的 SongBean。
 */
interface MusicProvider {
    /** 平台代号，见 [xyz.thewind.music.model.MusicSource] */
    val source: String

    /** 关键词搜索，[page] 从 0 开始。 */
    fun search(keyword: String, num: Int, page: Int): List<SongBean>

    /** 解析指定音质的下载链接，写入 [song] 的 downloadLinkMap 后返回。[song] 至少要有 songId。 */
    fun resolveDownloadLink(song: SongBean, quality: String): SongBean

    /** 批量解析下载链接，每首歌按自身的 quality 解析。只有部分平台支持。 */
    fun resolveDownloadLinks(songs: List<SongBean>): List<SongBean> = emptyList()

    /** 搜索并解析每首歌所有音质的下载链接。 */
    fun searchWithDownloadLinks(keyword: String, num: Int): List<SongBean>

    /** LRC 格式歌词，取不到返回空串。 */
    fun getLyric(songId: String): String

    /** 歌曲详情（歌名、歌手、封面等），不支持或失败返回 null。 */
    fun getSongDetail(songId: String): SongBean? = null
}
