package xyz.thewind.music.music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.impl.kg.KgMusic;
import xyz.thewind.music.music.impl.kw.KwMusic;
import xyz.thewind.music.music.impl.mg.MgMusic;
import xyz.thewind.music.music.impl.qq.QqMusic;
import xyz.thewind.music.music.impl.wy.WyMusic;
import xyz.thewind.music.util.Base64Converter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static xyz.thewind.music.music.common.SongUtil.*;

public class MusicDispatcher {
    private static final Map<String, IMusicApi> srcMap;

    static {
        srcMap = new HashMap<>();
        srcMap.put(SRC_QQ, new QqMusic());
        srcMap.put(SRC_WY, new WyMusic());
        srcMap.put(SRC_MG, new MgMusic());
        srcMap.put(SRC_KW, new KwMusic());
        srcMap.put(SRC_KG, new KgMusic());
    }

    private Map<String, String> configMap;

    public Map<String, String> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<String, String> configMap) {
        this.configMap = configMap;
    }

    //关键词搜索
    public List<SongBean> searchDispatcher(String songSrc, String keyword, int num) {
        List<SongBean> songList = new LinkedList<>();
        if (!srcMap.containsKey(songSrc)) {
            return songList;
        }
        IMusicApi IMusicApi = srcMap.get(songSrc);
        IMusicApi.setConfigMap(getConfigMap());
        songList = IMusicApi.searchMusic(keyword, num);
        return songList;
    }


    public SongBean downloadDispatcher(String songSrc, String songId, String quality) {
        SongBean songBean = new SongBean(songSrc, songId);
        if (!srcMap.containsKey(songSrc)) {
            return songBean;
        }
        IMusicApi IMusicApi = srcMap.get(songSrc);
        IMusicApi.setConfigMap(getConfigMap());
        return IMusicApi.searchDownloadLink(songBean, quality);
    }

    public List<SongBean> downloadDispatcher(String songSrc, String batch) {
        List<SongBean> downloadInfoList = new LinkedList<>();
        if (!srcMap.containsKey(songSrc)) {
            return downloadInfoList;
        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            downloadInfoList = gson.fromJson(Base64Converter.decode(batch), new TypeToken<List<SongBean>>() {
            }.getType());
            downloadInfoList = srcMap.get(songSrc).searchDownloadLink(downloadInfoList);
        } catch (Exception ignored) {

        }
        return downloadInfoList;
    }

    //关键词搜索
    public List<SongBean> searchDownDispatcher(String songSrc, String keyword, int num) {
        List<SongBean> songList = new LinkedList<>();
        if (!srcMap.containsKey(songSrc)) {
            return songList;
        }
        IMusicApi IMusicApi = srcMap.get(songSrc);
        IMusicApi.setConfigMap(getConfigMap());
        songList = IMusicApi.searchWithDownload(keyword, num);
        return songList;
    }

    public String searchLyricDispatcher(String songSrc, String songId) {
        if (!srcMap.containsKey(songSrc)) {
            return "";
        }
        IMusicApi IMusicApi = srcMap.get(songSrc);
        return IMusicApi.getLyric(songId);
    }

    public SongBean songDetailDispatcher(String songSrc, String songId) {
        if (!srcMap.containsKey(songSrc)) {
            return null;
        }
        IMusicApi IMusicApi = srcMap.get(songSrc);
        return IMusicApi.searchSongDetail(songId);
    }


}
