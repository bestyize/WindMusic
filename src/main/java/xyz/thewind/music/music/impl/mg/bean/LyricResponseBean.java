package xyz.thewind.music.music.impl.mg.bean;

public class LyricResponseBean {
    Data data = new Data();

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        String lyricLrc;

        public String getLyricLrc() {
            return lyricLrc;
        }

        public void setLyricLrc(String lyricLrc) {
            this.lyricLrc = lyricLrc;
        }
    }
}
