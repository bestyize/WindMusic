package xyz.thewind.music.support

import xyz.thewind.music.model.SongBean
import xyz.thewind.music.provider.MusicProvider
import xyz.thewind.music.store.KeyValueStore
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class InMemoryKeyValueStore(private var available: Boolean = true) : KeyValueStore {
    val data = ConcurrentHashMap<String, String>()

    override fun get(key: String): String? = if (available) data[key] else null

    override fun set(key: String, value: String, ttl: Duration?) {
        if (available) data[key] = value
    }

    override fun increment(key: String, delta: Long): Long? {
        if (!available) return null
        return data.merge(key, delta.toString()) { old, _ -> (old.toLong() + delta).toString() }!!.toLong()
    }

    override fun isAvailable(): Boolean = available
}

/** 不访问网络的假平台，便于测试分发和 Web 层。 */
class FakeMusicProvider(override val source: String = "TEST") : MusicProvider {
    var lyric = "[ti:晴天]\n[ar:周杰伦]\n[00:01.00]故事的小黄花"
    var detail: SongBean? = null

    override fun search(keyword: String, num: Int, page: Int): List<SongBean> =
        (1..num).map { SongBean(songSrc = source, songId = "$keyword-$page-$it", songName = keyword) }

    override fun resolveDownloadLink(song: SongBean, quality: String): SongBean =
        song.apply { downloadLinkMap[quality] = "https://example.com/${song.songId}.$quality.mp3" }

    override fun resolveDownloadLinks(songs: List<SongBean>): List<SongBean> =
        songs.map { resolveDownloadLink(it, it.quality ?: "PQ") }

    override fun searchWithDownloadLinks(keyword: String, num: Int): List<SongBean> =
        search(keyword, num, 0).map { resolveDownloadLink(it, "HQ") }

    override fun getLyric(songId: String): String = lyric

    override fun getSongDetail(songId: String): SongBean? = detail
}
