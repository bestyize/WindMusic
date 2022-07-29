package xyz.thewind.music.music.impl.wy.bean.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SongData {
    @SerializedName("songs")
    public List<SongDetail> songList;
    public int songCount;

    public List<SongDetail> getSongsList() {
        return songList;
    }

    public void setSongsList(List<SongDetail> songsList) {
        this.songList = songsList;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

}
