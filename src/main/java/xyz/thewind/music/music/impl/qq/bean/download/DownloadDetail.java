package xyz.thewind.music.music.impl.qq.bean.download;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.impl.qq.QqMusicModel;

public class DownloadDetail {

    @SerializedName("filename")
    public String fileName;
    @SerializedName("purl")
    public String downloadLink;
    @SerializedName("songmid")
    public String songId;
    public String qmdlfromtag;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadLink() {
        return QqMusicModel.baseDownloadServer + downloadLink + "&fromtag="+qmdlfromtag;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getQmdlfromtag() {
        return qmdlfromtag;
    }

    public void setQmdlfromtag(String qmdlfromtag) {
        this.qmdlfromtag = qmdlfromtag;
    }
}
