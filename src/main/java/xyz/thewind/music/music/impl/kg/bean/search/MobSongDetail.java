package xyz.thewind.music.music.impl.kg.bean.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MobSongDetail {
    int status;
    Data data = new Data();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("hash")
        String songId;
        @SerializedName("timelength")
        int duration;
        @SerializedName("audio_name")
        String songName;
        @SerializedName("author_name")
        String singerName;
        @SerializedName("author_id")
        String singerId;
        @SerializedName("img")
        String picUrl;
        @SerializedName("album_name")
        String albumName;
        @SerializedName("album_id")
        String albumId;
        List<Data.Authors> authors;

        public List<Data.Authors> getAuthors() {
            return authors;
        }

        public void setAuthors(List<Data.Authors> authors) {
            this.authors = authors;
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

        public String getSongId() {
            return songId;
        }

        public void setSongId(String songId) {
            this.songId = songId;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
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

        public static class Authors {
            @SerializedName("author_id")
            String singerId;
            @SerializedName("author_name")
            String singerName;

            public String getSingerId() {
                return singerId;
            }

            public void setSingerId(String singerId) {
                this.singerId = singerId;
            }

            public String getSingerName() {
                return singerName;
            }

            public void setSingerName(String singerName) {
                this.singerName = singerName;
            }
        }
    }
}
