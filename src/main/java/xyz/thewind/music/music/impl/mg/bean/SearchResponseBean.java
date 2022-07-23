package xyz.thewind.music.music.impl.mg.bean;

import com.google.gson.annotations.SerializedName;


import java.util.List;

public class SearchResponseBean {
    @SerializedName("musics")
    List<SongDetail> songDetailList;

    public List<SongDetail> getSongDetailList() {
        return songDetailList;
    }

    public void setSongDetailList(List<SongDetail> songDetailList) {
        this.songDetailList = songDetailList;
    }

}
