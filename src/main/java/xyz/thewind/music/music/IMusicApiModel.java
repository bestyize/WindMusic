package xyz.thewind.music.music;


import xyz.thewind.music.model.SongBean;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IMusicApiModel {
    /**
     * 搜索歌曲列表
     *
     * @param keyword 关键词
     * @param num     数量
     * @return 搜到的歌曲列表
     */
    List<SongBean> searchMusic(String keyword, int num);

    /**
     * 至少应该有个songid信息，获取全部链接
     *
     * @param songBean 歌曲信息，必须包含songId
     * @return
     */
    SongBean searchDownloadLink(SongBean songBean,String quality);

    /**
     * 根据songid批量获取
     * @param downloadInfoList
     * @return
     */
    default List<SongBean> searchDownloadLink(List<SongBean> downloadInfoList){
        return new LinkedList<>();
    }

    List<SongBean> searchWithDownload(String keyword, int num);

    default String searchMvUrl(String mvId){
        return "";
    }

    String getLyric(String lyricId);

    default SongBean searchSongDetail(String songId){
        return null;
    };

}
