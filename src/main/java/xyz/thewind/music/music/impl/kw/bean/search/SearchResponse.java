package xyz.thewind.music.music.impl.kw.bean.search;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
    @SerializedName("abslist")
    public List<SongDetail> songList = new ArrayList<>();

    public List<SongDetail> getSongList() {
        return songList;
    }

    public void setSongList(List<SongDetail> songList) {
        this.songList = songList;
    }
}
