package xyz.thewind.music.music.impl.kw.bean.download;

public class DownloadResp {

    public int code;
    public SongFileDetail data = new SongFileDetail();


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SongFileDetail getData() {
        return data;
    }

    public void setData(SongFileDetail data) {
        this.data = data;
    }
}
