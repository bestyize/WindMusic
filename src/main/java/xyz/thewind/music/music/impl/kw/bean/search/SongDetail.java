package xyz.thewind.music.music.impl.kw.bean.search;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.common.MusicCommonConfig;
import xyz.thewind.music.music.impl.kw.common.KwConfig;
import xyz.thewind.music.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SongDetail {

    @SerializedName("DC_TARGETID")
    public String songId;
    @SerializedName("SONGNAME")
    public String songName;
    @SerializedName("ALBUMID")
    public String albumId;
    @SerializedName("ALBUM")
    public String albumName;
    @SerializedName("ARTIST")
    public String singerName;
    @SerializedName("ARTISTID")
    public String singerId;
    @SerializedName("hts_MVPIC")
    public String picUrl;
    @SerializedName("N_MINFO")
    public String minInfo;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMinInfo() {
        return minInfo;
    }

    public void setMinInfo(String minInfo) {
        this.minInfo = minInfo;
    }

    public List<String> getSingersId() {
        List<String> singersId = new ArrayList<>();
        if (singerId == null) {
            return singersId;
        }
        String[] singers = singerId.split("&");
        singersId.addAll(Arrays.asList(singers));
        return singersId;
    }

    public List<String> getSingersName() {
        List<String> ret = new ArrayList<>();
        if (singerName == null) {
            return ret;
        }
        String[] singers = singerName.split("&");
        ret.addAll(Arrays.asList(singers));
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(new SongDetail().getQuality());
    }

    public String getQuality() {
        //minInfo =  "level:hr,bitrate:4000,format:flac,size:53.12Mb;level:ff,bitrate:2000,format:flac,size:27.29Mb;level:pp,bitrate:1000,format:ape,size:26.55Mb;level:p,bitrate:320,format:mp3,size:10.14Mb;level:h,bitrate:128,format:mp3,size:4.5Mb;level:s,bitrate:24,format:aac,size:780.33Kb";
        Map<String, String> paramsMap = StringUtils.strPairToMap(minInfo, ",", ":");
        String level = paramsMap.get(KwConfig.LEVEL);
        if (KwConfig.LEVEL_ZQ.equals(level)) {
            return MusicCommonConfig.QUALITY_ZQ;
        }
        if (KwConfig.LEVEL_SQ.equals(level)) {
            return MusicCommonConfig.QUALITY_SQ;
        }
        if (KwConfig.LEVEL_HQ.equals(level)) {
            return MusicCommonConfig.QUALITY_HQ;
        }
        if (KwConfig.LEVEL_PQ.equals(level)) {
            return MusicCommonConfig.QUALITY_PQ;
        }
        return MusicCommonConfig.QUALITY_LQ;
    }

    public Map<String, String> getQualityMap() {
        return StringUtils.strPairToMap(minInfo, ",", ":");
    }




}
