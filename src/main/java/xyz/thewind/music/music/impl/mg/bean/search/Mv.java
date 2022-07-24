package xyz.thewind.music.music.impl.mg.bean.search;

import com.google.gson.annotations.SerializedName;

public class Mv {
    public String id;
    @SerializedName("copyrightId")
    public String mvId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMvId() {
        return mvId;
    }

    public void setMvId(String mvId) {
        this.mvId = mvId;
    }
}
