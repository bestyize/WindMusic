package xyz.thewind.music.music.impl.wy.bean.search;

import com.google.gson.annotations.SerializedName;

public class Singer {
    @SerializedName("id")
    public long singerId;
    @SerializedName("name")
    public String singerName;
    public String picUrl;


    public long getSingerId() {
        return singerId;
    }

    public void setSingerId(long singerId) {
        this.singerId = singerId;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}