package xyz.thewind.music.music.impl.qq.bean.search;


public class SearchResponse {

    public int code;
    public SongData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SongData getData() {
        return data;
    }

    public void setData(SongData data) {
        this.data = data;
    }
}
