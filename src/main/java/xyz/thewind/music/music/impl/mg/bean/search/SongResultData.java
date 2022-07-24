package xyz.thewind.music.music.impl.mg.bean.search;

import java.util.ArrayList;
import java.util.List;

public class SongResultData {
    public String totalCount;
    public List<List<SongDetail>> resultList = new ArrayList<>();

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public List<List<SongDetail>> getResultList() {
        return resultList;
    }

    public void setResultList(List<List<SongDetail>> resultList) {
        this.resultList = resultList;
    }

}
