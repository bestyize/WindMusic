package xyz.thewind.music.provider.kw

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/** 期望值均由旧版 Java KwEncrypt 生成 */
class KwCryptoTest {

    @Test
    fun `short and block-aligned inputs`() {
        assertEquals("amNIiFPkHdE=", KwCrypto.encrypt(""))
        assertEquals("Gm3y+RY0NdU=", KwCrypto.encrypt("a"))
        // 恰好 8 字节时仍追加一个全 0 分组
        assertEquals("rkCv5z51z+lqY0iIU+Qd0Q==", KwCrypto.encrypt("abcdefgh"))
        assertEquals("xDYkFMt6l4/QZaiR67e5nw==", KwCrypto.encrypt("hello world"))
    }

    @Test
    fun `non-ascii input is truncated to char count`() {
        assertEquals("+GeCCLTOAPM=", KwCrypto.encrypt("中文测试"))
        assertEquals("SlS9RZZ5wKf9dnPvfgnnxQ==", KwCrypto.encrypt("混合Mixed文本123"))
        assertEquals("1pGjOceZzlc=", KwCrypto.encrypt("é"))
        assertEquals("xJjxbcRtEKE=", KwCrypto.encrypt("🎵music"))
    }

    @Test
    fun `long play url queries`() {
        assertEquals(
            "WdHQrW408QAI6L5nwYw/ym3MktJX06ENL9ns6PeUwP87Nj9WrEcZU+r0AcylI0/cyMRtCPNPxggU8IxqPX4zRdTGXG2vOGJuWqVwgYX2rI0CctPkpVfYgl+f8ZY6oZR4aZJGbD2LygREKBu2acaQJWl3k25Jvl4Ik8+upFDMN+MAXEYlRS8PsY6PdbDjzZr73BQ4n5pP7OPVTrhiHYKDrx97Ks9KiFkgGhzMzviG+R8MFIIO7p6B+3Y1XyVzyeITgHjA9uR9B3fUf3EqkJSZ81bqRWg3k71H5HyJYJt3p5uKeoDFZvLckdG2hjP47vP2SJEe7lk9qBFRRM+i3MyxdYYVRQcWA4/qEqHdnPC5mZ1fmIdlTuqxtg==",
            KwCrypto.encrypt(
                "prod=kwplayer_ar_10.3.6.3&corp=kuwo&newver=3&vipver=10.3.6.3&source=kwplayer_ar_10.3.6.3_40.apk&loginUid=123456&notrace=1&type=convert_url_with_sign&br=2000kflac&format=flac|mp3|aac&rid=80488731&priority=bitrate&loginSid=abcdef&mode=audition&timestamp=1700000000&isstar=false",
            ),
        )
        assertEquals(
            "WdHQrW408QAI6L5nwYw/ym3MktJX06ENL9ns6PeUwP87Nj9WrEcZU+r0AcylI0/cyMRtCPNPxggU8IxqPX4zRdTGXG2vOGJuWqVwgYX2rI0CctPkpVfYgl+f8ZY6oZR4aZJGbD2LygRcnQ/CttvGuhD7RN7RL8cd18NsawlyRyRtNBB3YsCCYBDqrgMYd8phJ00Sgt5pJiBE+3BMjOA9GkNqbbkwDANLkElcoqGPQax7KIyesVlT1AvfffW1txM+jmoLdKp1m2sVr7Nou5sq8ztDQZcvvCpvkpOeJKXCXTSLsFHXkZub/aVATAg9OPtkpMoU3gfTy7DsZR0s1iNul7L6DcxatiiP/VDnELHBCHM=",
            KwCrypto.encrypt(
                "prod=kwplayer_ar_10.3.6.3&corp=kuwo&newver=3&vipver=10.3.6.3&source=kwplayer_ar_10.3.6.3_40.apk&loginUid=null&notrace=1&type=convert_url_with_sign&br=320kmp3&format=flac|mp3|aac&rid=80488731&priority=bitrate&loginSid=null&mode=audition&timestamp=1700000000&isstar=false",
            ),
        )
    }
}
