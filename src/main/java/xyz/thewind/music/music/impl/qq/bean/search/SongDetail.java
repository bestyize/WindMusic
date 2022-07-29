package xyz.thewind.music.music.impl.qq.bean.search;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;

public class SongDetail {
    public Album album;
    public SongFile file;
    public String mid;
    public int interval;
    public Mv mv;
    public String name;
    @SerializedName("singer")
    public List<Singer> singers = new ArrayList<>();

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public SongFile getFile() {
        return file;
    }

    public void setFile(SongFile file) {
        this.file = file;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Mv getMv() {
        return mv;
    }

    public void setMv(Mv mv) {
        this.mv = mv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Singer> getSingers() {
        return singers;
    }

    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    public List<String> getSingersName() {
        List<String> singerList = new LinkedList<>();
        for (Singer singer : singers) {
            singerList.add(singer.name);
        }
        return singerList;
    }

    public List<String> getSingersId() {
        List<String> singerIdList = new LinkedList<>();
        for (Singer singer : singers) {
            singerIdList.add(singer.mid);
        }
        return singerIdList;
    }

    public String getQuality() {
        return file.getZqSize() > 0 ? QUALITY_ZQ : file.getSqSize() > 0 ? QUALITY_SQ : QUALITY_HQ;
    }

    public String getPicUrl() {
        return "https://y.gtimg.cn/music/photo_new/T002R800x800M000" + album.mid + ".jpg";
    }
}