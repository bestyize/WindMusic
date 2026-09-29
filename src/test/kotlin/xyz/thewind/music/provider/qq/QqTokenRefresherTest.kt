package xyz.thewind.music.provider.qq

import org.junit.jupiter.api.Test
import xyz.thewind.music.store.MusicSettings
import xyz.thewind.music.support.InMemoryKeyValueStore
import kotlin.test.assertEquals

class QqTokenRefresherTest {

    @Test
    fun `returns old token missed when no token is stored`() {
        val refresher = QqTokenRefresher(MusicSettings(InMemoryKeyValueStore(available = true)))
        assertEquals("old token missed", refresher.refresh())
    }

    @Test
    fun `returns old token missed when stored token is not json`() {
        val store = InMemoryKeyValueStore(available = true)
        store.data[MusicSettings.Keys.QQ_LOGIN_INFO] = "not json"
        assertEquals("old token missed", QqTokenRefresher(MusicSettings(store)).refresh())
    }

    @Test
    fun `returns redis closed when store is unavailable`() {
        val refresher = QqTokenRefresher(MusicSettings(InMemoryKeyValueStore(available = false)))
        assertEquals("redis closed", refresher.refresh())
    }
}
