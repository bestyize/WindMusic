package xyz.thewind.music.music.impl.mg

import com.google.gson.Gson
import xyz.thewind.music.model.SongBean
import xyz.thewind.music.music.IMusicApiModel
import xyz.thewind.music.music.common.MusicCommonConfig
import xyz.thewind.music.music.impl.mg.bean.LyricResponseBean
import xyz.thewind.music.music.impl.mg.bean.MgDownloadBean
import xyz.thewind.music.music.impl.mg.bean.search.MgSearchResp
import xyz.thewind.music.music.impl.mg.enc.MgEncrypt
import xyz.thewind.music.network.util.HttpUtil
import xyz.thewind.music.util.GsonUtil
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class MgMusicModel : IMusicApiModel {
    override fun searchMusic(keyword: String, num: Int, page: Int): List<SongBean> {
        return parseSearchResult(MgEncrypt.searchMusic(keyword, page, num))
    }

    override fun searchDownloadLink(songBean: SongBean, quality: String): SongBean {
        val downloadLinkMap = getDownloadLinkById(songBean.songId).downloadLinkMap
        downloadLinkMap[quality] ?: return songBean
        songBean.downloadLinkMap = downloadLinkMap
        return songBean
    }

    override fun searchWithDownload(keyword: String, num: Int): List<SongBean> {
        val songBeanList = searchMusic(keyword, num, 0)
        for (bean in songBeanList) {
            searchDownloadLink(bean, "HQ")
        }
        return songBeanList
    }

    fun doSearchMusic(keyword: String?, num: Int, page: Int): String {
        val key = URLEncoder.encode(keyword, StandardCharsets.UTF_8)
        val api =
            "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/content/search_all.do?isCopyright=1&isCorrect=1&pageNo=" + (page + 1) + "&pageSize=" + num + "&sort=0&text=" + key + "&searchSwitch={\"song\":1,\"tagSong\":1}"
        return HttpUtil.get(api, null)
    }

    private fun parseSearchResult(response: String): List<SongBean> {
        val songBeanList: MutableList<SongBean> = LinkedList()
        val resp = GsonUtil.fromJson(response, MgSearchResp::class.java) ?: return songBeanList
        for (details in resp.result.resultList) {
            val detail = details[0]
            val song = SongBean.Builder()
                .songSrc(MusicCommonConfig.SRC_MG)
                .songId(detail.songId)
                .singersName(detail.singersName)
                .songName(detail.songName)
                .singersId(detail.singersId)
                .albumId(detail.albumId)
                .albumName(detail.albumName)
                .quality(detail.quality)
                .picUrl(detail.picUrl)
                .lrcUrl(detail.lyricUrl)
                .mvUrl(detail.mvId)
                .build()
            songBeanList.add(song)
        }
        return songBeanList
    }

    override fun getLyric(lyricId: String): String {
        val url = "https://m.music.migu.cn/migu/remoting/cms_detail_tag?cpid=$lyricId"
        val headerMap: MutableMap<String, String> = HashMap()
        headerMap["Referer"] = "https://m.music.migu.cn/v3/music/song/$lyricId"
        val response = HttpUtil.get(url, headerMap)
        try {
            val bean = GsonUtil.fromJson(response, LyricResponseBean::class.java)
            if (bean.data.lyricLrc != null) {
                return bean.data.lyricLrc!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun searchSongDetail(songId: String): SongBean {
        return getDownloadLinkById(songId)
    }

    private fun getDownloadLinkById(songid: String): SongBean {
        val songBean = SongBean(MusicCommonConfig.SRC_MG, songid)
        val link = baseContentLink + songid
        val headerMap: MutableMap<String, String> = HashMap()
        headerMap["User-Agent"] = MusicCommonConfig.USER_AGENT_ANDROID
        headerMap["Host"] = "m.music.migu.cn"
        val response = HttpUtil.get(link, headerMap)
        val mgDownloadBean = Gson().fromJson(response, MgDownloadBean::class.java)
        if (mgDownloadBean == null || mgDownloadBean.resource == null || mgDownloadBean.resource.size == 0) {
            return songBean
        }
        val musicDetail = mgDownloadBean.resource[0]
        val rateFormatList = musicDetail.rateFormats
        songBean.lrcUrl = mgDownloadBean.resource[0].lrcUrl
        val map = songBean.downloadLinkMap
        if (rateFormatList != null) {
            for (formats in rateFormatList) {
                map[formats.formatType] = formats.url
            }
        }
        if (musicDetail.eqFormat != null) {
            map[MusicCommonConfig.QUALITY_EQ] = musicDetail.eqFormat.url
        }
        songBean.songName = musicDetail.songName
        songBean.albumName = musicDetail.albumName
        songBean.singersName = listOf(musicDetail.singerName)
        songBean.singersId = listOf(musicDetail.singerId)
        if (musicDetail.albumImgs.size > 0) {
            songBean.picUrl = musicDetail.albumImgs[0].img
        }
        return songBean
    }

    companion object {
        //或者：https://app.c.nf.migu.cn/MIGUM3.0/v2.0/content/querySongBySongId.do?songId=1136379763
        private const val baseContentLink =
            "https://c.musicapp.migu.cn/MIGUM3.0/v1.0/content/resourceinfo.do?ua=Android_migu&version=5.0&resourceType=2&copyrightId="
    }
}