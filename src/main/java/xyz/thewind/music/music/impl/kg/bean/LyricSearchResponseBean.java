package xyz.thewind.music.music.impl.kg.bean;

import java.util.List;

public class LyricSearchResponseBean {
    int errcode;
    List<Lyric> candidates;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public List<Lyric> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Lyric> candidates) {
        this.candidates = candidates;
    }

    public class Lyric {
        String id;
        String accesskey;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAccesskey() {
            return accesskey;
        }

        public void setAccesskey(String accesskey) {
            this.accesskey = accesskey;
        }
    }
}
