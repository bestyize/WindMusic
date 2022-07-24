package xyz.thewind.music.music.impl.kg.bean.search;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.common.MusicCommonConfig;

import java.util.ArrayList;
import java.util.List;

public class SongDetail {

    @SerializedName("SongName")
    public String songName;
    @SerializedName("AlbumName")
    public String albumName;
    @SerializedName("AlbumID")
    public String albumId;

    @SerializedName("ResFileHash")
    public String zqHash;
    @SerializedName("SQFileHash")
    public String sqHash;
    @SerializedName("HQFileHash")
    public String hqHash;
    @SerializedName("FileHash")
    public String pqHash;

    @SerializedName("Singers")
    public List<Singer> singers = new ArrayList<>();
    @SerializedName("mvdata")
    public List<Mv> mvs = new ArrayList<>();


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getZqHash() {
        return zqHash;
    }

    public void setZqHash(String zqHash) {
        this.zqHash = zqHash;
    }

    public String getSqHash() {
        return sqHash;
    }

    public void setSqHash(String sqHash) {
        this.sqHash = sqHash;
    }

    public String getHqHash() {
        return hqHash;
    }

    public void setHqHash(String hqHash) {
        this.hqHash = hqHash;
    }

    public String getPqHash() {
        return pqHash;
    }

    public void setPqHash(String pqHash) {
        this.pqHash = pqHash;
    }

    public List<Singer> getSingers() {
        return singers;
    }

    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    public List<Mv> getMvs() {
        return mvs;
    }

    public void setMvs(List<Mv> mvs) {
        this.mvs = mvs;
    }

    public String getSongId() {
        return pqHash + "-" + hqHash + "-" + sqHash + "-" + zqHash;
    }

    public List<String> getSingersName() {
        List<String> singersName = new ArrayList<>();
        for (Singer singer : singers) {
            singersName.add(singer.getName());
        }
        return singersName;
    }

    public List<String> getSingersId() {
        List<String> singersId = new ArrayList<>();
        for (Singer singer : singers) {
            singersId.add(singer.getId() + "");
        }
        return singersId;
    }

    public String getQuality() {
        if (zqHash != null && zqHash.length() > 0) {
            return MusicCommonConfig.QUALITY_ZQ;
        }
        if (sqHash != null && sqHash.length() > 0) {
            return MusicCommonConfig.QUALITY_SQ;
        }

        if (hqHash != null && hqHash.length() > 0) {
            return MusicCommonConfig.QUALITY_HQ;
        }

        return MusicCommonConfig.QUALITY_PQ;
    }

    public String getMvId() {
        List<String> list = new ArrayList<>();
        if (mvs.size() == 0) {
            return "";
        }
        return mvs.get(0).getMvId();
    }

}
