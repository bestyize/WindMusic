package xyz.thewind.music.music.impl.mg.bean;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.common.SongUtil;

public class SongDetail {
    @SerializedName("copyrightId")
    String songId;
    @SerializedName("title")
    String songName;
    String albumName;
    String albumId;
    @SerializedName("singerName")
    String singerName;
    String singerId;
    @SerializedName("mp3")
    String lqLink;
    @SerializedName("lyrics")
    String lyricUrl;
    @SerializedName("cover")
    String picUrl;
    @SerializedName("hasSQqq")
    String quality;
    @SerializedName("has24Bitqq")
    String hasZq;
    @SerializedName("has3Dqq")
    String hasEq;
    @SerializedName("mvId")
    String mvId;

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

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
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

    public String getLqLink() {
        return lqLink;
    }

    public void setLqLink(String lqLink) {
        this.lqLink = lqLink;
    }

    public String getLyricUrl() {
        return lyricUrl;
    }

    public void setLyricUrl(String lyricUrl) {
        this.lyricUrl = lyricUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getQuality() {
        String qualityTmp = SongUtil.QUALITY_EQ;
        if (hasEq == null) {
            qualityTmp = SongUtil.QUALITY_ZQ;
        }
        if (hasZq == null) {
            qualityTmp = SongUtil.QUALITY_SQ;
        }
        if (quality == null) {
            qualityTmp = SongUtil.QUALITY_HQ;
        }
        return qualityTmp;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getMvId() {
        return mvId;
    }

    public void setMvId(String mvId) {
        this.mvId = mvId;
    }
}
