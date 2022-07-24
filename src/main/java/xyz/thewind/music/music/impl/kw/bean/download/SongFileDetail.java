package xyz.thewind.music.music.impl.kw.bean.download;

import com.google.gson.annotations.SerializedName;

public class SongFileDetail {

    @SerializedName("bitrate")
    public int bitRate;
    public String format;
    public String url;

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
