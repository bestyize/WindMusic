package xyz.thewind.music.music.impl.qq.bean.search;


import java.util.ArrayList;
import java.util.List;

public class SongList {

    public List<SongDetail> list = new ArrayList<>();

    public List<SongDetail> getList() {
        return list;
    }

    public void setList(List<SongDetail> list) {
        this.list = list;
    }
}
