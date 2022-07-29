package xyz.thewind.music.music.impl.wy.bean;

import java.util.HashMap;
import java.util.Map;

public class LyricResponse {
    public int code;
    public Map<String, Object> lrc = new HashMap<>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, Object> getLrc() {
        return lrc;
    }

    public void setLrc(Map<String, Object> lrc) {
        this.lrc = lrc;
    }
}
