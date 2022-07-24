package xyz.thewind.music.music.impl.kg.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;

public class SearchResponseBean {
    int status;
    String error;
    Data data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("info")
        List<Data.SongDetail> songDetailList;

        public List<Data.SongDetail> getSongDetailList() {
            return songDetailList;
        }

        public void setSongDetailList(List<Data.SongDetail> songDetailList) {
            this.songDetailList = songDetailList;
        }

        public class SongDetail {
            int price;
            @SerializedName("singername")
            String singers;
            @SerializedName("songname")
            String songName;
            String songId;
            @SerializedName("hash")
            String pqHash;
            @SerializedName("320hash")
            String hqHash;
            @SerializedName("sqhash")
            String sqHash;
            @SerializedName("filesize")
            long pqSize;
            @SerializedName("320filesize")
            long hqSize;
            @SerializedName("sqfilesize")
            long sqSize;
            @SerializedName("album_id")
            String albumId;
            @SerializedName("album_name")
            String albumName;
            @SerializedName("mvhash")
            String mvId;
            @SerializedName("duration")
            int timeSpan;

            String quality;

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public String getSingers() {
                return singers;
            }

            public void setSingers(String singers) {
                this.singers = singers;
            }

            public String getSongName() {
                return songName;
            }

            public void setSongName(String songName) {
                this.songName = songName;
            }

            public String getSongId() {
                return getPqHash() + "-" + getHqHash() + "-" + getSqHash();
            }

            public void setSongId(String songId) {
                this.songId = songId;
            }

            public String getPqHash() {
                return pqHash;
            }

            public void setPqHash(String pqHash) {
                this.pqHash = pqHash;
            }

            public String getHqHash() {
                return hqHash;
            }

            public void setHqHash(String hqHash) {
                this.hqHash = hqHash;
            }

            public String getSqHash() {
                return sqHash;
            }

            public void setSqHash(String sqHash) {
                this.sqHash = sqHash;
            }

            public long getPqSize() {
                return pqSize;
            }

            public void setPqSize(long pqSize) {
                this.pqSize = pqSize;
            }

            public long getHqSize() {
                return hqSize;
            }

            public void setHqSize(long hqSize) {
                this.hqSize = hqSize;
            }

            public long getSqSize() {
                return sqSize;
            }

            public void setSqSize(long sqSize) {
                this.sqSize = sqSize;
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

            public String getMvId() {
                return mvId;
            }

            public void setMvId(String mvId) {
                this.mvId = mvId;
            }

            public int getTimeSpan() {
                return timeSpan;
            }

            public void setTimeSpan(int timeSpan) {
                this.timeSpan = timeSpan;
            }

            public String getQuality() {
                return (getSqHash() != null && getSqHash().length() > 0) ? QUALITY_SQ : (getHqHash() != null && getHqHash().length() > 0) ? QUALITY_HQ : QUALITY_PQ;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }
        }
    }
}
