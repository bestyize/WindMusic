package xyz.thewind.music.provider.kw

import org.junit.jupiter.api.Test
import xyz.thewind.music.model.Quality
import kotlin.test.assertEquals

/** 期望值均由旧版 Java 代码生成 */
class KwApiTest {

    @Test
    fun `search url encodes keyword`() {
        assertEquals(
            "http://search.kuwo.cn/r.s?user=0&all=%E5%91%A8%E6%9D%B0%E4%BC%A6+%E6%99%B4%E5%A4%A9&pn=1&rn=20&uid=0&ver=kwplayer_ar_10.1.5.0&vipver=1&newver=3&correct=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1&searchapi=5",
            KwApi.searchUrl("周杰伦 晴天", 20, 1),
        )
        assertEquals(
            "http://search.kuwo.cn/r.s?user=0&all=a%26b%2Bc%3Dd%2F%3F&pn=1&rn=20&uid=0&ver=kwplayer_ar_10.1.5.0&vipver=1&newver=3&correct=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1&searchapi=5",
            KwApi.searchUrl("a&b+c=d/?", 20, 1),
        )
    }

    @Test
    fun `play url query keeps legacy parameter order`() {
        assertEquals(
            "prod=kwplayer_ar_10.3.6.3&corp=kuwo&newver=3&vipver=10.3.6.3&source=kwplayer_ar_10.3.6.3_40.apk&loginUid=123456&notrace=1&type=convert_url_with_sign&br=2000kflac&format=flac|mp3|aac&rid=80488731&priority=bitrate&loginSid=abcdef&mode=audition&timestamp=1700000000&isstar=false",
            KwApi.playUrlQuery("80488731", Quality.SQ, "123456", "abcdef", 1700000000L),
        )
    }

    @Test
    fun `play url query renders missing values as null`() {
        assertEquals(
            "prod=kwplayer_ar_10.3.6.3&corp=kuwo&newver=3&vipver=10.3.6.3&source=kwplayer_ar_10.3.6.3_40.apk&loginUid=null&notrace=1&type=convert_url_with_sign&br=null&format=flac|mp3|aac&rid=null&priority=bitrate&loginSid=null&mode=audition&timestamp=1700000000&isstar=false",
            KwApi.playUrlQuery(null, Quality.EQ, null, null, 1700000000L),
        )
    }

    @Test
    fun `play url is base plus encrypted query`() {
        assertEquals(
            "http://nmobi.kuwo.cn/mobi.s?f=kuwo&q=WdHQrW408QAI6L5nwYw/ym3MktJX06ENL9ns6PeUwP87Nj9WrEcZU+r0AcylI0/cyMRtCPNPxggU8IxqPX4zRdTGXG2vOGJuWqVwgYX2rI0CctPkpVfYgl+f8ZY6oZR4aZJGbD2LygREKBu2acaQJWl3k25Jvl4Ik8+upFDMN+MAXEYlRS8PsY6PdbDjzZr73BQ4n5pP7OMRfFjvSWWr4B97Ks9KiFkgGhzMzviG+R8MFIIO7p6B+3Y1XyVzyeITgHjA9uR9B3fUf3EqkJSZ81bqRWg3k71H5HyJYJt3p5uKeoDFZvLckdG2hjP47vP2SJEe7lk9qBFRRM+i3MyxdYYVRQcWA4/qEqHdnPC5mZ1fmIdlTuqxtg==",
            KwApi.playUrl("80488731", Quality.ZQ, "123456", "abcdef", 1700000000L),
        )
    }
}
