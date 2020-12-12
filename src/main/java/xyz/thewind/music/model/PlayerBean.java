package xyz.thewind.music.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

public class PlayerBean {
    private String title;
    private String author;
    private String pic;
    private String url;
    private String lyc;

    public PlayerBean(String title, String author, String pic, String url, String lyc) {
        this.title = title;
        this.author = author;
        this.pic = pic;
        this.url = url;
        this.lyc = lyc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLyc() {
        return lyc;
    }

    public void setLyc(String lyc) {
        this.lyc = lyc;
    }
}
