package xyz.thewind.music.music.impl.qq.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    public int code;
    public DataWrapper data = new DataWrapper();


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataWrapper getData() {
        return data;
    }

    public void setData(DataWrapper data) {
        this.data = data;
    }

    public static class DataWrapper {
        public DataWrapper2 data = new DataWrapper2();

        public DataWrapper2 getData() {
            return data;
        }

        public void setData(DataWrapper2 data) {
            this.data = data;
        }
    }

    public static class DataWrapper2 {
        public Body body = new Body();

        public Body getBody() {
            return body;
        }

        public void setBody(Body body) {
            this.body = body;
        }
    }

    public static class Body {
        public Song song = new Song();

        public Song getSong() {
            return song;
        }

        public void setSong(Song song) {
            this.song = song;
        }
    }

    public static class Song {
        public List<SongItem> list = new ArrayList<>();

        public List<SongItem> getList() {
            return list;
        }

        public void setList(List<SongItem> list) {
            this.list = list;
        }
    }

    public static class SongItem {
        public Album album;
        public File file;
        public String mid;
        public int interval;
        public Mv mv;
        public String name;
        public List<Singer> singer = new ArrayList<>();

        public Album getAlbum() {
            return album;
        }

        public void setAlbum(Album album) {
            this.album = album;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public Mv getMv() {
            return mv;
        }

        public void setMv(Mv mv) {
            this.mv = mv;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Singer> getSinger() {
            return singer;
        }

        public void setSinger(List<Singer> singer) {
            this.singer = singer;
        }
    }

    public static class Album {
        public int id;
        public String mid;
        public String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class File {

        @SerializedName("media_mid")
        public String mediaMid;
        @SerializedName("size_96ogg")
        long lqSize;
        @SerializedName("size_128mp3")
        long pqSize;
        @SerializedName("size_320mp3")
        long hqSize;
        @SerializedName("size_flac")
        long sqSize;
        @SerializedName("size_hires")
        long zqSize;

        public String getMediaMid() {
            return mediaMid;
        }

        public void setMediaMid(String mediaMid) {
            this.mediaMid = mediaMid;
        }

        public long getLqSize() {
            return lqSize;
        }

        public void setLqSize(long lqSize) {
            this.lqSize = lqSize;
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

        public long getZqSize() {
            return zqSize;
        }

        public void setZqSize(long zqSize) {
            this.zqSize = zqSize;
        }
    }

    public static class Mv {
        public String vid;

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }
    }

    public static class Singer {
        public int id;
        public String mid;
        public String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
