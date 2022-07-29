package xyz.thewind.music.music.impl.qq.bean.download;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DownloadResponseDataInfo {

    public long expiration;
    @SerializedName("midurlinfo")
    public List<DownloadDetail> songDetailList;

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public List<DownloadDetail> getSongDetailList() {
        return songDetailList;
    }

    public void setSongDetailList(List<DownloadDetail> songDetailList) {
        this.songDetailList = songDetailList;
    }
}
