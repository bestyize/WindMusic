package xyz.thewind.music.music.impl.mg.bean.search;

public class MgSearchResp {
    public String code;
    public String info;

    public SongResultData songResultData = new SongResultData();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public SongResultData getSongResultData() {
        return songResultData;
    }

    public void setSongResultData(SongResultData songResultData) {
        this.songResultData = songResultData;
    }
}
