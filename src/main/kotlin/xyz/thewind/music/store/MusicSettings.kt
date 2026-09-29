package xyz.thewind.music.store

import org.springframework.stereotype.Component

/**
 * 运行时可修改的音乐配置：各平台 VIP 账号 Cookie 以及强制音源，保存在 [KeyValueStore] 中。
 * 通过 `/api/configupdate` 写入，各平台 Provider 在请求时实时读取。
 */
@Component
class MusicSettings(private val store: KeyValueStore) {

    object Keys {
        const val SONG_SRC = "songSrc"
        const val QQ_LOGIN_INFO = "qqmusic_wx_login_info"
        const val WY_MUSIC_U = "wy_music_u"
        const val KG_UID = "kg_uid"
        const val KG_TOKEN = "kg_token"
        const val KW_UID = "kw_uid"
        const val KW_SID = "kw_sid"

        val ALL = listOf(KG_UID, KG_TOKEN, WY_MUSIC_U, SONG_SRC, QQ_LOGIN_INFO, KW_UID, KW_SID)
    }

    /** 服务端强制使用的音源；非空时覆盖请求里的 `src` 参数。 */
    val forcedSource: String? get() = get(Keys.SONG_SRC)?.takeIf { it.isNotEmpty() }

    /** QQ 音乐登录信息（JSON），由 QQ Provider 解析和定时刷新。 */
    var qqLoginInfo: String?
        get() = get(Keys.QQ_LOGIN_INFO)
        set(value) = set(Keys.QQ_LOGIN_INFO, value.orEmpty())

    val wyMusicU: String? get() = get(Keys.WY_MUSIC_U)
    val kgUid: String? get() = get(Keys.KG_UID)
    val kgToken: String? get() = get(Keys.KG_TOKEN)
    val kwUid: String? get() = get(Keys.KW_UID)
    val kwSid: String? get() = get(Keys.KW_SID)

    val isWritable: Boolean get() = store.isAvailable()

    fun get(key: String): String? = store.get(key)

    fun set(key: String, value: String) = store.set(key, value)

    fun snapshot(): Map<String, String?> = Keys.ALL.associateWith { get(it) }
}
