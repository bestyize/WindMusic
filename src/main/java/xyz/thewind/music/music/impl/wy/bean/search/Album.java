package xyz.thewind.music.music.impl.wy.bean.search;

import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("id")
    public long albumId;
    @SerializedName("name")
    public String albumName;
    @SerializedName("picUrl")
    public String albumPicUrl;

    public long publishTime;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getAlbumPicUrl() {
        if (albumPicUrl != null && albumPicUrl.contains("http://")) {
            albumPicUrl = albumPicUrl.replace("http://", "https://");
        }
        return albumPicUrl;
    }

    public void setAlbumPicUrl(String albumPicUrl) {
        this.albumPicUrl = albumPicUrl;
    }
}