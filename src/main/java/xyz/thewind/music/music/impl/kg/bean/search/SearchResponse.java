package xyz.thewind.music.music.impl.kg.bean.search;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
    @SerializedName("error_msg")
    public String errMsg;
    public Data data = new Data();


    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        public List<SongDetail> lists = new ArrayList<>();

        public List<SongDetail> getLists() {
            return lists;
        }

        public void setLists(List<SongDetail> lists) {
            this.lists = lists;
        }
    }
}
