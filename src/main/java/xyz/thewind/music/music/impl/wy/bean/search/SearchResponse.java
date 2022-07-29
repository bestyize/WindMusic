package xyz.thewind.music.music.impl.wy.bean.search;

import java.util.LinkedList;
import java.util.List;

public class SearchResponse {

    public boolean needLogin;
    public SongData result = new SongData();
    public int code;

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public SongData getResult() {
        return result;
    }

    public void setResult(SongData result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
