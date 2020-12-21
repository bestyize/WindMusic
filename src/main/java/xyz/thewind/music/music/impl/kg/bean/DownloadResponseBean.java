package xyz.thewind.music.music.impl.kg.bean;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.impl.kg.KgMusic;

import java.util.List;

import static xyz.thewind.music.music.common.SongUtil.QUALITY_SQ;

public class DownloadResponseBean {
    public int status;
    long fileSize;
    List<String> url;
    String extName;
    int bitRate;
    @SerializedName("timeLength")
    int timeSpan;

    String quality;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public int getBitRate() {
        return bitRate > 320000 ? 999000 : bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(int timeSpan) {
        this.timeSpan = timeSpan;
    }

    //kg音乐部分音乐源bitRate不正确，这时候要用扩展名区分（扩展名和音乐也不完全对应）
    public String getQuality() {
        int bitRate = getBitRate();
        if (bitRate > 320000) {
            bitRate = 999000;
        } else if (bitRate > 192000) {
            bitRate = 320000;
        } else if (bitRate > 128000) {
            bitRate = 192000;
        } else {
            bitRate = 128000;
        }
        return getExtName().equals("flac") ? QUALITY_SQ : KgMusic.qualityMap.get(bitRate);
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
