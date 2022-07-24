package xyz.thewind.music.music.impl.mg.bean.search;

import com.google.gson.annotations.SerializedName;

public class ImgItem {
    public String imageSizeTYpe;
    @SerializedName("img")
    public String imgUrl;

    public String getImageSizeTYpe() {
        return imageSizeTYpe;
    }

    public void setImageSizeTYpe(String imageSizeTYpe) {
        this.imageSizeTYpe = imageSizeTYpe;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
