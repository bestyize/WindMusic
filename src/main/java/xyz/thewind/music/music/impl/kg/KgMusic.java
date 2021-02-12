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
            String requestLink="http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword="+ URLEncoder.encode(keyword,"utf-8") +"&page=1&pagesize="+num+"&area_code=0";
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

    private String getCoverByHash(String hash){
        String link="https://expendablekmrcdn.kugou.com/container/v1/image?data=%5B%7B%22hash%22%3A%22"+hash+"%22%7D%5D";
        String response=HttpRequestHelper.downloadWebSiteUseGet(link,null);
        if(response.contains("\"sizable_avatar\":\"")){
            response=response.substring(response.indexOf("\"sizable_avatar\":\"")+"\"sizable_avatar\":\"".length());
            response=response.substring(0,response.indexOf("\""));
            response=response.replace("{size}","480");
        }else {
            response="";
        }
        return response;

    }

    private List<String> parseSingerList(String singer){
        String[] singers=singer.split("，");
        return new LinkedList<>(Arrays.asList(singers));
    }
    private List<String> parseSingersId(){
        return new LinkedList<>();//kg音乐没有返回歌手ID
    }

    public static String downloadRequest(String hash,String quality){
        //return ThirdPartApi.getPlayUrl(hash,quality);
        return downloadRequest(hash);
    }

    private static String downloadRequest(String hash){
        String dfid="-";
        String pid="2";
        String mid="0";
        String cmd="26";
        String clienttime=Long.valueOf(System.currentTimeMillis() / 1000)+"";

        String uuid="0";
        String area_code="1";
        String behavior="play";
        String appid="1005";
        String module="";
        String vipType="6";
        String ptype="0";
        String userid=configMap.getOrDefault("kg_uid","1538743078");
        String token=configMap.getOrDefault("kg_token","fa044ef0ad5e094b2c10f1567c1686ad7ab84caa862e160cef2232e86f39e832");
        String mtype="2";
        String album_id="2900867";
        String pidversion="3001";
        String pidVersionSecret="57ae12eb6890223e355ccfcb74edf70d";
        String clientver="10479";
        String version="10479";
        String album_audio_id="album_audio_id";

        Map<String,Object> map=new HashMap<>();
        map.put("dfid", dfid);
        map.put("hash",hash);
        map.put("mtype",mtype);
        map.put("album_id",album_id);
        map.put("album_audio_id",album_audio_id);
        map.put("module",module);
        map.put("behavior",behavior);
        map.put("cmd",cmd);

        map.put("uuid", uuid);
        map.put("clientver", clientver);
        map.put("clienttime", clienttime);

        map.put("pid",pid);
        map.put("appid", appid);
        map.put("mid", mid);
        map.put("version",version);
        map.put("token",token);

        map.put("vipType",vipType);
        map.put("userid",userid);
        map.put("area_code",area_code);
        map.put("dfid",dfid);
        map.put("ptype",ptype);
        map.put("pidversion",pidversion);
        map.put("key",Md5Encrypt.convertToMd5(hash+pidVersionSecret+appid+mid+userid));
        String appkey="OIlwieks28dk2k092lksi2UIkp";
        map.put("signature",getSignature(appkey,map));
        String reqLink="https://gateway.kugou.com/v3/url"+getGetRequestParams(map);
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("x-router","tracker.kugou.com");
        String response=HttpRequestHelper.downloadWebSiteUseGet(reqLink,headerMap);
        if(response.contains("http:")&&response.contains("\"")){
            response=response.substring(response.indexOf("\"url\":[\"")+"\"url\":[\"".length());
            response=response.substring(0,response.indexOf("\"")).replaceAll("\\\\","");
        }else {
            response="";
        }
        return response;

    }

    private static String getGetRequestParams(Map<String,Object> map) {
        if (map == null || map.size() < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        Set<String> keySet = map.keySet();
        for (String str : keySet) {
            sb.append(str);
            sb.append("=");
            sb.append(map.get(str));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }



    private static String getSignature(String str, Map<String, Object> map) {
        if (map == null || map.size() < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = map.keySet();
        String[] strArr = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(strArr, String::compareTo);
        for (String str3 : strArr) {
            sb.append(str3);
            sb.append("=");
            sb.append(map.get(str3));
        }
        return Md5Encrypt.convertToMd5(str + sb.toString() + str);
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
