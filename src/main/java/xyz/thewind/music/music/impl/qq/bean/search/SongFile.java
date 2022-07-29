package xyz.thewind.music.music.impl.qq.bean.search;

import com.google.gson.annotations.SerializedName;

public class SongFile {

    @SerializedName("media_mid")
    public String mediaMid;
    @SerializedName("size_96ogg")
    public long lqSize;
    @SerializedName("size_128mp3")
    public long pqSize;
    @SerializedName("size_320mp3")
    public long hqSize;
    @SerializedName("size_flac")
    public long sqSize;
    @SerializedName("size_hires")
    public long zqSize;

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