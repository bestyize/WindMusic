package xyz.thewind.music.music.impl.qq.bean.download;

import com.google.gson.annotations.SerializedName;

public class DownloadResponseData {
    public int code;
    @SerializedName("data")
    public DownloadResponseDataInfo dataInfo = new DownloadResponseDataInfo();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DownloadResponseDataInfo getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(DownloadResponseDataInfo dataInfo) {
        this.dataInfo = dataInfo;
    }
}
