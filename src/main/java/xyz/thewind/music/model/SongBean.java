package xyz.thewind.music.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SongBean implements Serializable {

    private static final long serialVersionUID = 876323294945176354L;

    private String songSrc;//歌曲来源，QQ音乐等
    private String songId;//歌曲ID
    private String songName;//歌曲名
    private List<String> singersId;
    private List<String> singersName;//歌手名
    private String albumId;//专辑Id
    private String albumName;//专辑名
    private String picUrl;//歌曲封面
    private String lrcUrl;//歌词链接
    private String mvUrl;//MV链接
    private String songLink;//歌曲链接
    private String quality;//最高质量
    private Map<String, String> downloadLinkMap;
//    private String lqDownloadLink;//128或者最低品质音乐
//    private String pqDownloadLink;//128或者192的MP3
//    private String hqDownloadLink;//320的音乐
//    private String sqDownloadLink;//FLAC格式的音乐

    //此处构造函数只传入songSrc,songId,仅供搜索下载链接时候使用
    public SongBean(String songSrc, String songId) {
        this.songSrc = songSrc;
        this.songId = songId;
    }

    public SongBean(String songSrc, String songId, String quality) {
        this.songSrc = songSrc;
        this.songId = songId;
        this.quality = quality;
    }

    private SongBean(Builder builder) {
        setSongSrc(builder.songSrc);
        setSongId(builder.songId);
        setSongName(builder.songName);
        setSingersId(builder.singersId);
        setSingersName(builder.singersName);
        setAlbumId(builder.albumId);
        setAlbumName(builder.albumName);
        setPicUrl(builder.picUrl);
        setLrcUrl(builder.lrcUrl);
        setMvUrl(builder.mvUrl);
        setSongLink(builder.songLink);
        setQuality(builder.quality);
        setDownloadLinkMap(builder.downloadLinkMap);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSongSrc() {
        return songSrc;
    }

    public void setSongSrc(String songSrc) {
        this.songSrc = songSrc;
    }

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

    public List<String> getSingersId() {
        return singersId;
    }

    public void setSingersId(List<String> singersId) {
        this.singersId = singersId;
    }

    public List<String> getSingersName() {
        return singersName;
    }

    public void setSingersName(List<String> singersName) {
        this.singersName = singersName;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(String lrcUrl) {
        this.lrcUrl = lrcUrl;
    }

    public String getMvUrl() {
        return mvUrl;
    }

    public void setMvUrl(String mvUrl) {
        this.mvUrl = mvUrl;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Map<String, String> getDownloadLinkMap() {
        if (downloadLinkMap == null) {
            downloadLinkMap = new HashMap<>();
        }
        return downloadLinkMap;
    }

    public void setDownloadLinkMap(Map<String, String> downloadLinkMap) {
        this.downloadLinkMap = downloadLinkMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongBean songBean = (SongBean) o;
        return Objects.equals(songSrc, songBean.songSrc) &&
                Objects.equals(songId, songBean.songId) &&
                Objects.equals(songName, songBean.songName) &&
                Objects.equals(singersName, songBean.singersName) &&
                Objects.equals(albumName, songBean.albumName) &&
                Objects.equals(picUrl, songBean.picUrl) &&
                Objects.equals(lrcUrl, songBean.lrcUrl) &&
                Objects.equals(mvUrl, songBean.mvUrl) &&
                Objects.equals(songLink, songBean.songLink) &&
                Objects.equals(quality, songBean.quality) &&
                Objects.equals(downloadLinkMap, songBean.downloadLinkMap);
    }

    @Override
    public String toString() {
        return "SongBean{" +
                "songSrc='" + songSrc + '\'' +
                ", songId='" + songId + '\'' +
                ", songName='" + songName + '\'' +
                ", singersName=" + singersName +
                ", albumName='" + albumName + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", lrcUrl='" + lrcUrl + '\'' +
                ", mvUrl='" + mvUrl + '\'' +
                ", songLink='" + songLink + '\'' +
                ", quality='" + quality + '\'' +
                ", downloadLinkMap=" + downloadLinkMap +
                '}';
    }

    public static final class Builder {
        private String songSrc;
        private String songId;
        private String songName;
        private List<String> singersId;
        private List<String> singersName;
        private String albumId;
        private String albumName;
        private String picUrl;
        private String lrcUrl;
        private String mvUrl;
        private String songLink;
        private String quality;
        private Map<String, String> downloadLinkMap;

        public Builder() {
        }

        public Builder songSrc(String val) {
            songSrc = val;
            return this;
        }

        public Builder songId(String val) {
            songId = val;
            return this;
        }

        public Builder songName(String val) {
            songName = val;
            return this;
        }

        public Builder singersId(List<String> val) {
            singersId = val;
            return this;
        }

        public Builder singersName(List<String> val) {
            singersName = val;
            return this;
        }

        public Builder albumId(String val) {
            albumId = val;
            return this;
        }

        public Builder albumName(String val) {
            albumName = val;
            return this;
        }

        public Builder picUrl(String val) {
            picUrl = val;
            return this;
        }

        public Builder lrcUrl(String val) {
            lrcUrl = val;
            return this;
        }

        public Builder mvUrl(String val) {
            mvUrl = val;
            return this;
        }

        public Builder songLink(String val) {
            songLink = val;
            return this;
        }

        public Builder quality(String val) {
            quality = val;
            return this;
        }

        public Builder downloadLinkMap(Map<String, String> val) {
            downloadLinkMap = val;
            return this;
        }

        public SongBean build() {
            return new SongBean(this);
        }
    }
}