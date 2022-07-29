package xyz.thewind.music.music.impl.wy.bean.search;

import com.google.gson.annotations.SerializedName;

public class Quality {
    @SerializedName("br")
    public String bitRate;
    @SerializedName("size")
    public long fileSize;

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}