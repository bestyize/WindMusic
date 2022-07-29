package xyz.thewind.music.music.impl.qq.bean.download;

import com.google.gson.annotations.SerializedName;

public class DownloadResponse {
    public int code;
    @SerializedName("queryvkey")
    public DownloadResponseData data = new DownloadResponseData();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DownloadResponseData getData() {
        return data;
    }

    public void setData(DownloadResponseData data) {
        this.data = data;
    }
}
