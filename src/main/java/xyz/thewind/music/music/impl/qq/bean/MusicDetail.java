package xyz.thewind.music.music.impl.qq.bean;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.impl.qq.bean.search.SongDetail;

import java.util.ArrayList;
import java.util.List;

public class MusicDetail {
    public SongInfo songinfo = new SongInfo();
    int code;

    public static class SongInfo {
        public Data data = new Data();
        int code;

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


    }

    public static class Data {
        @SerializedName("track_info")
        public SongDetail songDetail = new SongDetail();
        public List<Info> info = new ArrayList<>();

        public List<Info> getInfo() {
            return info;
        }

        public void setInfo(List<Info> info) {
            this.info = info;
        }

        public SongDetail getSongDetail() {
            return songDetail;
        }

        public void setSongDetail(SongDetail songDetail) {
            this.songDetail = songDetail;
        }

    }


    public static class Info {
        public String title;
        public String type;
        public List<Content> content = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Content> getContent() {
            return content;
        }

        public void setContent(List<Content> content) {
            this.content = content;
        }

    }

    public static class Content {
        public String value;
        public String mid;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }
    }
}
