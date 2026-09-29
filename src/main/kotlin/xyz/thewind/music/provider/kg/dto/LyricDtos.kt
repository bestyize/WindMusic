package xyz.thewind.music.provider.kg.dto

/** krcs.kugou.com/search 的响应：按 hash 搜到的候选歌词 */
class LyricSearchResponse {
    var candidates: List<LyricCandidate>? = null
}

class LyricCandidate {
    var id: String? = null
    var accesskey: String? = null
}

/** lyrics.kugou.com/download 的响应，[content] 是 Base64 编码的 LRC 歌词 */
class LyricDownloadResponse {
    var content: String? = null
}
