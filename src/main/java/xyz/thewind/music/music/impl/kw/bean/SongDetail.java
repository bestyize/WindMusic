package xyz.thewind.music.music.impl.kw.bean;

import com.google.gson.annotations.SerializedName;

public class SongDetail {
    int code;
    Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("artist")
        String singerName;
        String pic;
        @SerializedName("songId")
        long rid;
        int duration;
        @SerializedName("artistid")
        long singerId;
        @SerializedName("album")
        String albumName;
        @SerializedName("albumid")
        public long albumId;
        public boolean hasLossless;
        @SerializedName("name")
        String songName;

        public String getSingerName() {
            return singerName;
        }

        public void setSingerName(String singerName) {
            this.singerName = singerName;
        }

        public String getPic() {
            if (pic != null && pic.contains("http://")) {
                pic = pic.replace("http://", "https://");
            }
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public long getRid() {
            return rid;
        }

        public void setRid(long rid) {
            this.rid = rid;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public long getSingerId() {
            return singerId;
        }

        public void setSingerId(long singerId) {
            this.singerId = singerId;
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

        public boolean isHasLossless() {
            return hasLossless;
        }

        public void setHasLossless(boolean hasLossless) {
            this.hasLossless = hasLossless;
        }

        public String getSongName() {
            return songName;
        }

        public void setSongName(String songName) {
            this.songName = songName;
        }
    }
}
