package xyz.thewind.music.music.impl.qq;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.music.impl.qq.bean.*;
import xyz.thewind.music.music.impl.qq.module.RequestModule;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;

import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUsePost;
import static xyz.thewind.music.music.common.SongUtil.*;


public class QqMusic implements MusicHelper {
    private static final Logger logger= LogManager.getLogger(QqMusic.class);
    private static final HashMap<String, String> qualityMap;
    public static final String baseDownloadServer="http://ws.stream.qqmusic.qq.com/";

    private static final Map<String,String> configMap=new HashMap<>();

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(QUALITY_SQ,"F000songid.flac");
        qualityMap.put(QUALITY_HQ,"M800songid.mp3");
        qualityMap.put(QUALITY_PQ,"M500songid.mp3");
        qualityMap.put(QUALITY_LQ,"C400songid.m4a");
    }

    @Override
    public void setConfigMap(Map<String, String> configMaps) {
        if(configMaps!=null){
            configMap.clear();
            configMap.putAll(configMaps);
        }

    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String response=qqSearchRequest(keyword,num);
        return qqSongSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        //String downloadLink=getDownloadLinkBySongId(songBean.getSongId(),quality);
        String downloadLink= RequestModule.getPlayUrl(songBean.getSongId(),quality);
        songBean.getDownloadLinkMap().put(quality,downloadLink);
        return songBean;
    }

    @Override
    public List<SongBean> searchDownloadLink(List<SongBean> downloadInfoList) {
        //List<DownloadInfo> downloadInfoList
        return getDownloadLinkBySongIdList(downloadInfoList);
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList=searchMusic(keyword,num);
        for (SongBean bean:songBeanList){
            searchDownloadLink(bean,QUALITY_SQ);
            searchDownloadLink(bean,QUALITY_HQ);           
            searchDownloadLink(bean,QUALITY_LQ);
            searchDownloadLink(bean,QUALITY_PQ);
        }
        return songBeanList;
    }

    private static String downloadLinkRequestParamsBuilder(List<SongBean> downloadInfoList) {
        String qqUin=configMap.get("qqmusic_uin")==null?"0":configMap.get("qqmusic_uin");
        DownloadRequestBean requestBean=new DownloadRequestBean();
        requestBean.queryvkey.param.setUin(qqUin);
        List<String> filenames=new LinkedList<>();
        List<String> musicfiles=new LinkedList<>();
        List<String> songmid=new LinkedList<>();
        List<Integer> songtype=new LinkedList<>();
        for (SongBean info:downloadInfoList){
            filenames.add(qualityMap.get(info.getQuality()).replace("songid",info.getSongId()));
            musicfiles.add(qualityMap.get(info.getQuality()).replace("songid",info.getSongId()));
            songmid.add(info.getSongId());
            songtype.add(1);
        }
        requestBean.queryvkey.param.setFilename(filenames);
        requestBean.queryvkey.param.setMusicfile(musicfiles);
        requestBean.queryvkey.param.setSongmid(songmid);
        requestBean.queryvkey.param.setSongtype(songtype);
        return new Gson().toJson(requestBean);
    }

    private String qqSearchRequest(String keyword, int num){
        String response="";
        try {
            String link="https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&n="+num+"&w="+ URLEncoder.encode(keyword,"utf-8") +"&format=json";
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent",USER_AGENT_ANDROID);
            response=downloadWebSiteUseGet(link,headerMap);
        }catch (Exception e){
            logger.error("encode failed");
        }
        return response;

    }
    /**
     *        this.songSrc = songSrc;
     *         this.songId = songId;
     *         this.songName = songName;
     *         this.singersName = singersName;
     *         this.albumName = albumName;
     *         this.quality=quality;
     *         this.picUrl = picUrl;
     *         this.lrcUrl = lrcUrl;
     *         this.mvUrl = mvUrl;
     * @param response
     * @return
     */
    private List<SongBean> qqSongSearchRespProcess(String response) {
        List<SongBean> list = new LinkedList<>();
        if (response.length() == 0) {
            return list;
        }
        try {
            SearchResponseBean searchResponseBean= GsonConverter.fromJson(response, SearchResponseBean.class);
            if(searchResponseBean.getCode()==0){
                List<SearchResponseBean.Data.Song.SongDetail> songDetailList=searchResponseBean.data.song.songDetailList;
                for (SearchResponseBean.Data.Song.SongDetail detail:songDetailList){
                    String picUrl="";
                    if(detail.getAlbumId()!=null&&detail.getAlbumId().length()>0){
                        picUrl="https://y.gtimg.cn/music/photo_new/T002R800x800M000"+detail.getAlbumId()+".jpg";
                    }
                    SongBean songBean=new SongBean(SRC_QQ,
                            detail.getSongId(),
                            detail.getSongName(),
                            parseSingersId(detail.getSingers()),
                            parseSingers(detail.getSingers()),
                            detail.getAlbumId(),
                            detail.getAlbumName(),
                            detail.getSqSize()>0?QUALITY_SQ:QUALITY_HQ,
                            picUrl,
                            "",
                            detail.getMvId());
                    list.add(songBean);
                }
            }
        }catch (Exception ignored){

        }
        return list;
    }

    private List<String> parseSingers(List<SearchResponseBean.Data.Song.SongDetail.Singer> singers){
        List<String> singerList=new LinkedList<>();
        for (SearchResponseBean.Data.Song.SongDetail.Singer singer:singers){
            singerList.add(singer.getSingerName());
        }
        return singerList;
    }

    private List<String> parseSingersId(List<SearchResponseBean.Data.Song.SongDetail.Singer> singers){
        List<String> singerIdList=new LinkedList<>();
        for (SearchResponseBean.Data.Song.SongDetail.Singer singer:singers){
            singerIdList.add(singer.getSingerId());
        }
        return singerIdList;
    }

    public static String getDownloadLinkBySongId(String songid,String quality){
        SongBean downloadInfo=new SongBean(SRC_QQ,songid,quality);
        List<SongBean> downloadInfoList=new LinkedList<>();
        downloadInfoList.add(downloadInfo);
        String requestParams=downloadLinkRequestParamsBuilder(downloadInfoList);
        String response=downloadRequest(requestParams);
        String downloadLink="";
        DownloadBean downloadBean=GsonConverter.fromJson(response,DownloadBean.class);
        if(downloadBean!=null&&downloadBean.data!=null&&downloadBean.data.data.songDetailList.size()>0){
            List<DownloadBean.Data1.Data.SongDetail> songDetailList=downloadBean.data.data.songDetailList;
            DownloadBean.Data1.Data.SongDetail songDetail=songDetailList.get(0);
            downloadLink=songDetail.getDownloadLink();
        }
        return downloadLink;


    }

    public static List<SongBean> getDownloadLinkBySongIdList(List<SongBean> downloadInfoList){
        String requestParams=downloadLinkRequestParamsBuilder(downloadInfoList);
        String response=downloadRequest(requestParams);
        DownloadBean downloadBean=GsonConverter.fromJson(response,DownloadBean.class);
        if(downloadBean!=null&&downloadBean.data!=null&&downloadBean.data.data.songDetailList.size()>0){
            List<DownloadBean.Data1.Data.SongDetail> songDetailList=downloadBean.data.data.songDetailList;
            for (int i=0;i<songDetailList.size();i++){
                DownloadBean.Data1.Data.SongDetail detail= songDetailList.get(i);
                downloadInfoList.get(i).getDownloadLinkMap().put(downloadInfoList.get(i).getQuality(),detail.getDownloadLink());
            }
        }
        return downloadInfoList;


    }

    private static String downloadRequest(String params){
        String qqUin=configMap.get("qqmusic_uin")==null?"0":configMap.get("qqmusic_uin");
        String qqKey=configMap.get("qqmusic_key")==null?"0":configMap.get("qqmusic_key");
        //qqmusic_key=Q_H_L_2upCI-50ee22y3KWLlZ2odnazfqjWuAyEynHQnNCJv-oUTh4wXl0kzk1vO1PVG1;uin=3011429848
        String qqCookie="qqmusic_key="+qqKey+";qqmusic_uin="+qqUin;
        //String qqCookie="qqmusic_key=Q_H_L_2CntI-50eSSxCL7uzvUr7dtyRlorofXpLJLFQ-AjNyuJdo_ugx9mK9SIgT2ifs6; qqmusic_miniversion=91; qqmusic_uin=3011429848;";
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.27 Safari/537.36 Edg/88.0.705.18");
        headerMap.put("Host","u.y.qq.com");
        headerMap.put("Connection","Keep-Alive");
        headerMap.put("Cookie",qqCookie);
        headerMap.put("Accept","application/json");
        return downloadWebSiteUsePost("https://u.y.qq.com/cgi-bin/musicu.fcg?pcachetime=1607655349",params,headerMap);
    }

    @Override
    public String getLyric(String lyricId) {
        String baseLink="https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?g_tk=5381&format=json&songmid="+lyricId;
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Referer","https://y.qq.com/portal/player.html");
        String encodeLyric=HttpRequestHelper.downloadWebSiteUseGet(baseLink,headerMap);
        try {
            LyricResponseBean bean=GsonConverter.fromJson(encodeLyric,LyricResponseBean.class);
            return Base64Converter.decode(bean.getLyric());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String data="{\"songinfo\":{\"module\":\"music.pf_song_detail_svr\",\"method\":\"get_song_detail\",\"param\":{\"song_mid\":\""+songId+"\"}}}";
        try {
            String encode = URLEncoder.encode(data,"UTF-8");
            String api="https://u.y.qq.com/cgi-bin/musicu.fcg";
            String response=HttpRequestHelper.downloadWebSiteUseGet(api+"?data="+encode,null);
            SongDetail detail=GsonConverter.fromJson(response,SongDetail.class);
            SongDetail.Songinfo.Data.TrackInfo trackInfo=detail.songinfo.data.trackInfo;
            SongBean songBean=new SongBean("QQ",songId);
            songBean.setSongId(trackInfo.getMid());
            songBean.setSongName(trackInfo.getName());
            List<SongDetail.Songinfo.Data.TrackInfo.Singer> singerList=trackInfo.getSinger();
            List<String> singersName=new ArrayList<>();
            List<String> singersId=new ArrayList<>();
            for (SongDetail.Songinfo.Data.TrackInfo.Singer singer:singerList){
                singersName.add(singer.name);
                singersId.add(singer.mid);
            }
            songBean.setSingersName(singersName);
            songBean.setSingersId(singersId);
            SongDetail.Songinfo.Data.TrackInfo.Album album=trackInfo.getAlbum();
            songBean.setAlbumId(album.getMid());
            songBean.setAlbumName(album.getName());
            if(album.getMid()!=null&&album.getMid().length()>0){
                songBean.setPicUrl("https://y.gtimg.cn/music/photo_new/T002R800x800M000"+album.getMid()+".jpg");
            }
            List<SongDetail.Songinfo.Data.Info> infos=detail.songinfo.data.info;
            for (SongDetail.Songinfo.Data.Info info:infos){
                if(info.type.equals("lyric")){
                    if(info.content.size()>0){
                        songBean.setLrcUrl(info.content.get(0).value);
                    }

                }
            }
            songBean.setMvUrl(trackInfo.getMv().vid);
            return songBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
