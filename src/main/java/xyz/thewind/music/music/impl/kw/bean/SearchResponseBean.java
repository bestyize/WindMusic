package xyz.thewind.music.music.impl.kw.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponseBean {

    public int code;
    public long curTime;
    public Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        public List<Data.SongDetail> list;
        private String total;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<Data.SongDetail> getList() {
            return list;
        }

        public void setList(List<Data.SongDetail> list) {
            this.list = list;
        }

        public class SongDetail {
            @SerializedName("rid")
            public String songId;
            @SerializedName("name")
            public String songName;
            @SerializedName("artist")
            public String singerName;
            @SerializedName("pic")
            public String picUrl;

            public int duration;
            public boolean hasLossless;
            public int hasMv;
            public String releaseDate;
            @SerializedName("album")
            public String albumName;
            @SerializedName("albumid")
            private long albumId;
            @SerializedName("artistid")
            private int singerId;
            @SerializedName("albumPic")
            private String albumPicUrl;
            private String songTimeMinutes;
            private boolean isListenFee;

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

            public String getSingerName() {
                return singerName;
            }

            public void setSingerName(String singerName) {
                this.singerName = singerName;
            }

            public String getPicUrl() {
                if (picUrl != null && picUrl.contains("http://")) {
                    picUrl = picUrl.replace("http://", "https://");
                }
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public boolean isHasLossless() {
                return hasLossless;
            }

            public void setHasLossless(boolean hasLossless) {
                this.hasLossless = hasLossless;
            }

            public int getHasMv() {
                return hasMv;
            }

            public void setHasMv(int hasMv) {
                this.hasMv = hasMv;
            }

            public String getReleaseDate() {
                return releaseDate;
            }

            public void setReleaseDate(String releaseDate) {
                this.releaseDate = releaseDate;
            }

            public String getAlbumName() {
                return albumName;
            }

            public void setAlbumName(String albumName) {
                this.albumName = albumName;
            }

            public long getAlbumId() {
                return albumId;
            }

            public void setAlbumId(long albumId) {
                this.albumId = albumId;
            }

            public int getSingerId() {
                return singerId;
            }

            public void setSingerId(int singerId) {
                this.singerId = singerId;
            }

            public String getAlbumPicUrl() {
                return albumPicUrl;
            }

            public void setAlbumPicUrl(String albumPicUrl) {
                this.albumPicUrl = albumPicUrl;
            }

            public String getSongTimeMinutes() {
                return songTimeMinutes;
            }

            public void setSongTimeMinutes(String songTimeMinutes) {
                this.songTimeMinutes = songTimeMinutes;
            }

            public boolean isListenFee() {
                return isListenFee;
            }

            public void setListenFee(boolean listenFee) {
                isListenFee = listenFee;
            }
        }
    }


}
