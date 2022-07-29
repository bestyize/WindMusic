package xyz.thewind.music.music.impl.wy.bean.search;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;

public class SongDetail {

    @SerializedName("id")
    public long songId;
    @SerializedName("name")
    public String songName;
    @SerializedName("ar")
    public List<Singer> singers;
    @SerializedName("al")
    public Album album;

    @SerializedName("sq")
    public Quality sQuality;
    @SerializedName("h")
    public Quality hQuality;
    @SerializedName("p")
    public Quality pQuality;
    @SerializedName("l")
    public Quality lQuality;

    @SerializedName("hr")
    public Quality zqQuality;
    @SerializedName("mv")
    public long mvId;

    public Privilege privilege;

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public List<Singer> getSingers() {
        return singers;
    }

    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Quality getsQuality() {
        return sQuality;
    }

    public void setsQuality(Quality sQuality) {
        this.sQuality = sQuality;
    }

    public Quality gethQuality() {
        return hQuality;
    }

    public void sethQuality(Quality hQuality) {
        this.hQuality = hQuality;
    }

    public Quality getpQuality() {
        return pQuality;
    }

    public void setpQuality(Quality pQuality) {
        this.pQuality = pQuality;
    }

    public Quality getlQuality() {
        return lQuality;
    }

    public void setlQuality(Quality lQuality) {
        this.lQuality = lQuality;
    }

    public Quality getZqQuality() {
        return zqQuality;
    }

    public void setZqQuality(Quality zqQuality) {
        this.zqQuality = zqQuality;
    }

    public long getMvId() {
        return mvId;
    }

    public void setMvId(long mvId) {
        this.mvId = mvId;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public List<String> getSingersName() {
        List<String> singerList = new LinkedList<>();
        for (Singer singer : getSingers()) {
            singerList.add(singer.getSingerName());
        }
        return singerList;
    }

    public List<String> getSingersId() {
        List<String> singerIdList = new LinkedList<>();
        for (Singer singer : getSingers()) {
            singerIdList.add(singer.getSingerId() + "");
        }
        return singerIdList;
    }

    public String getQuality() {
        if (privilege.maxbr < 999000) {
            return QUALITY_HQ;
        }
        if (zqQuality != null) {
            return QUALITY_ZQ;
        }
        return QUALITY_SQ;
    }
}
