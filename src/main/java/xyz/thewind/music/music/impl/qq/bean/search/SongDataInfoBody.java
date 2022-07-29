package xyz.thewind.music.music.impl.qq.bean.search;

import com.google.gson.annotations.SerializedName;

public class SongDataInfoBody {
    @SerializedName("song")
    public SongList songList = new SongList();

    public SongList getSongList() {
        return songList;
    }

    public void setSongList(SongList songList) {
        this.songList = songList;
    }
}
