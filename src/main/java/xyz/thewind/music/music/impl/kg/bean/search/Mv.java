package xyz.thewind.music.music.impl.kg.bean.search;

import com.google.gson.annotations.SerializedName;

public class Mv {
    @SerializedName("hash")
    public String mvId;

    public String getMvId() {
        return mvId;
    }

    public void setMvId(String mvId) {
        this.mvId = mvId;
    }
}
