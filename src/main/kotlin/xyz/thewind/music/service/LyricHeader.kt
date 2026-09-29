package xyz.thewind.music.service

import xyz.thewind.music.common.javaSplit
import xyz.thewind.music.model.MusicSource

/**
 * 从 LRC 歌词头部解析歌名和歌手。多数平台第 1、2 行是 `[ti:歌名]`、`[ar:歌手]`，酷狗整体下移一行。
 */
internal object LyricHeader {
    fun songName(src: String, lyric: String): String = line(lyric, if (src == MusicSource.KG) 1 else 0)

    fun singerName(src: String, lyric: String): String = line(lyric, if (src == MusicSource.KG) 2 else 1)

    fun line(lyric: String, index: Int): String {
        val lines = lyric.replace("\r\n", "\n").replace("[", "").javaSplit("\n")
        val line = lines.getOrNull(index) ?: return ""
        return when {
            line.startsWith("ti:") -> line.replace("ti:", "").replace("]", "")
            line.startsWith("ar:") -> line.replace("ar:", "").replace("]", "")
            else -> line.substring(line.indexOf("]") + 1)
        }
    }
}
