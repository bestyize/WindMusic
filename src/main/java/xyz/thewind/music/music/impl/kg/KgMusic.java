package xyz.thewind.music.music.impl.kg;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.music.impl.kg.bean.*;
import xyz.thewind.music.music.impl.kg.module.ThirdPartApi;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.music.common.SongUtil.*;


public class KgMusic implements MusicHelper {
    private static final Logger logger= LogManager.getLogger(KgMusic.class);
    public static final HashMap<Integer, String> qualityMap;

    private static final Map<String,String> configMap=new HashMap<>();

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(999000,QUALITY_SQ);
        qualityMap.put(320000,QUALITY_HQ);
        qualityMap.put(192000,QUALITY_PQ);
        qualityMap.put(128000,QUALITY_LQ);
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
        String response=searchRequest(keyword,num);
        return songSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String songId=songBean.getSongId();
        if(songId!=null&&songId.length()>=32){
            String[] hashArr=songBean.getSongId().split("-");
            Map<String,String> map=songBean.getDownloadLinkMap();
            songBean.setDownloadLinkMap(map);
            String hash=hashArr[0];
            if(quality.equals("SQ")&&hashArr.length==3){
                hash=hashArr[2];
            }else if(quality.equals("HQ")&&hashArr.length>=2){
                hash=hashArr[1];
            }
            map.put(quality,downloadRequest(hash,quality));
        }
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList=searchMusic(keyword,num);
        for (SongBean bean:songBeanList){
            String[] hashArr=bean.getSongId().split("-");
            String qualitys[]={"PQ","HQ","SQ"};
            Map<String,String> map=bean.getDownloadLinkMap();
            bean.setDownloadLinkMap(map);
            for (int i=0;i<hashArr.length;i++){
                map.put(qualitys[i],downloadRequest(hashArr[i],qualitys[i]));
            }
        }
        return songBeanList;
    }

    public static String searchRequest(String keyword, int num){
        String response=null;
        try {
            String requestLink="http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword="+ URLEncoder.encode(keyword,"utf-8") +"&page=1&pagesize="+num;
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent",USER_AGENT_ANDROID);
            response= HttpRequestHelper.downloadWebSiteUseGet(requestLink,headerMap);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.toString());
        }
        return response;
    }

    /**
     *         this.songSrc = songSrc;
     *         this.songId = songId;
     *         this.songName = songName;
     *         this.singersId=singersId;
     *         this.singersName = singersName;
     *         this.albumId=albumId;
     *         this.albumName = albumName;
     *         this.quality=quality;
     *         this.picUrl = picUrl;
     *         this.lrcUrl = lrcUrl;
     *         this.mvUrl = mvUrl;
     * @param response
     * @return
     */
    private List<SongBean> songSearchRespProcess(String response){

        List<SongBean> list=new LinkedList<>();
        if(response.length()==0){
            return list;
        }
        try {
            SearchResponseBean responseBean= GsonConverter.fromJson(response,SearchResponseBean.class);
            List<SearchResponseBean.Data.SongDetail> songDetailList=responseBean.getData().getSongDetailList();
            for (SearchResponseBean.Data.SongDetail detail:songDetailList){
                SongBean songBean=new SongBean(
                        SRC_KG,
                        detail.getSongId(),
                        detail.getSongName(),
                        parseSingersId(),
                        parseSingerList(detail.getSingers()),
                        detail.getAlbumId(),
                        detail.getAlbumName(),
                        detail.getQuality(),
                        "",
                        "",
                        detail.getMvId()
                );
                list.add(songBean);
            }
        }catch (Exception e){
            logger.error("parse search response failed");
        }
        return list;
    }

    private List<String> parseSingerList(String singer){
        String[] singers=singer.split("，");
        return new LinkedList<>(Arrays.asList(singers));
    }
    private List<String> parseSingersId(){
        return new LinkedList<>();//kg音乐没有返回歌手ID
    }


    public SongBean downloadResponseProcess(SongBean songBean, String response){
        try {
            response=response.replace("<!--KG_TAG_RES_START-->","").replace("<!--KG_TAG_RES_END-->","");
            DownloadResponseBean responseBean= GsonConverter.fromJson(response,DownloadResponseBean.class);
            Map<String,String> map=songBean.getDownloadLinkMap();
            songBean.setDownloadLinkMap(map);
            if(responseBean.status==1){
                map.put(responseBean.getQuality(),responseBean.getUrl().get(1).replaceAll("\\\\",""));
                songBean.setQuality(responseBean.getQuality());
            }else {
                logger.warn("is vip song");
            }
        }catch (Exception e){
            logger.error("failed");
        }

        return songBean;
    }

    public static String downloadRequest(String hash,String quality){
        return ThirdPartApi.getPlayUrl(hash,quality);
    }

//    public static String downloadRequest(String hash){
//        Map<String,String> headerMap=new HashMap<>();
//        headerMap.put("User-Agent","Android511-AndroidPhone-10259-14-0-NetMusic-wifi");
//        headerMap.put("KG-FAKE",configMap.getOrDefault("kg_uid","1717986917"));
//        headerMap.put("KG-RC","1");
//        headerMap.put("KG-RF","00877b56");
//        headerMap.put("KG-THash","1509c84");
//        headerMap.put("x-router","tracker.kugou.com");
//        headerMap.put("Host","gateway.kugou.com");
//        return HttpRequestHelper.downloadWebSiteUseGet(downloadLinkBuilder(hash,false),headerMap);
//    }

    public static String downloadLinkBuilder(String hash,boolean usePid){

        String pidVersionSecret= "57ae12eb6890223e355ccfcb74edf70d";
        String uid=configMap.getOrDefault("kg_uid","1717986917");
        if(uid==null){
            uid="0";
        }
        String token=configMap.get("kg_token");
        if(token==null){
            token="0";
        }
        String pidVersion="3001";
        String appid="1005";//1005是安卓手机版kg音乐
        String mid="0";
        String baseLink="https://gateway.kugou.com/i/v2/?";
        String vipType="6";
        String dfid="0";

        StringBuilder sb=new StringBuilder();
        sb.append("pid=2&cmd=26&area_code=1&behavior=play&module=&ptype=0&mtype=0&album_id=39027869&version=10259&album_audio_id=272185068&with_res_tag=1");
        sb.append("&mid=").append(mid);
        sb.append("&vipType=").append(vipType);
        sb.append("&hash=").append(hash);
        sb.append("&dfid=").append(dfid);
        sb.append("&token=").append(token);
        sb.append("&appid=").append(appid);
        sb.append("&userid=").append(uid);
        if(usePid){
            sb.append("&pidversion=").append(pidVersion);
            String key= Md5Encrypt.convertToMd5(hash+pidVersionSecret+appid+mid+uid);
            sb.append("&key=").append(key);
        }else {
            sb.append("&key=").append(Md5Encrypt.convertToMd5(hash + "kgcloudv2" + appid + mid + uid));
        }
        return baseLink+sb.toString();
    }


    /**
     * 也可以先：https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=0CCA78AF607DC52400D31C18BF036277&mid=12345
     * 获取album_id
     * 然后：https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=0CCA78AF607DC52400D31C18BF036277&mid=12345&album_id=40602950
     * 获取歌词
     * @param lyricId
     * @return
     */
    @Override
    public String getLyric(String lyricId) {
        if(lyricId==null||lyricId.length()<32){
            return "";
        }
        if(lyricId.contains("-")){
            lyricId=lyricId.substring(0,lyricId.indexOf("-"));
        }

        String searchLink="http://krcs.kugou.com/search?ver=1&hash="+lyricId;
        String searchResponse=HttpRequestHelper.downloadWebSiteUseGet(searchLink,null);
        try {
                LyricSearchResponseBean bean=GsonConverter.fromJson(searchResponse,LyricSearchResponseBean.class);
                if(bean!=null&&bean.getCandidates()!=null&&bean.getCandidates().size()>0){
                    LyricSearchResponseBean.Lyric lyric=bean.getCandidates().get(0);
                    String requestUrl="http://lyrics.kugou.com/download?&fmt=lrc&ver=1&id="+lyric.getId()+"&accesskey="+lyric.getAccesskey();
                    LyricData lyricData=GsonConverter.fromJson(HttpRequestHelper.downloadWebSiteUseGet(requestUrl,null),LyricData.class);
                    if(lyricData!=null){
                        return Base64Converter.decode(lyricData.getContent());
                    }

                }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String sid=songId;
        if(songId.length()<20){
            return null;
        }
        if(songId.contains("-")){
            songId=songId.substring(0,songId.indexOf("-"));
        }
        String url="https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash="+songId.toUpperCase(Locale.ROOT)+"&mid=12345";
        try {
            String response=HttpRequestHelper.downloadWebSiteUseGet(url,null);
            SongDetail.Data data=GsonConverter.fromJson(response,SongDetail.class).getData();
            if(data!=null){
                SongBean bean=new SongBean("KG",sid);
                bean.setSongName(data.getSongName());
                bean.setPicUrl(data.getPicUrl());
                List<SongDetail.Data.Authors> authors= data.getAuthors();
                List<String> singersName=new ArrayList<>();
                List<String> singerIds=new ArrayList<>();
                for (SongDetail.Data.Authors author:authors){
                    singerIds.add(author.getSingerId());
                    singersName.add(author.getSingerName());
                }
                bean.setSingersId(singerIds);
                bean.setSingersName(singersName);
                bean.setAlbumName(data.getAlbumName());
                bean.setAlbumId(data.getAlbumId());
                return bean;
            }
        }catch (Exception r){
            r.printStackTrace();
        }
        return null;
    }


}
