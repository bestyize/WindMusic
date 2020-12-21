package xyz.thewind.music.music.impl.qq.bean;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.impl.qq.QqMusic;

import java.util.List;

public class DownloadBean {
    int code;
    @SerializedName("queryvkey")
    public Data1 data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public  class Data1 {
        int code;
        public Data1.Data data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Data1.Data getData() {
            return data;
        }

        public void setData(Data1.Data data) {
            this.data = data;
        }

        public  class Data {
            long expiration;
            @SerializedName("midurlinfo")
            public List<Data1.Data.SongDetail> songDetailList;

            public long getExpiration() {
                return expiration;
            }

            public void setExpiration(long expiration) {
                this.expiration = expiration;
            }

            public List<Data1.Data.SongDetail> getSongDetailList() {
                return songDetailList;
            }

            public void setSongDetailList(List<Data1.Data.SongDetail> songDetailList) {
                this.songDetailList = songDetailList;
            }

            public  class SongDetail {
                @SerializedName("filename")
                String fileName;
                @SerializedName("purl")
                String downloadLink;
                @SerializedName("songmid")
                String songId;

                public String getFileName() {
                    return fileName;
                }

                public void setFileName(String fileName) {
                    this.fileName = fileName;
                }

                public String getDownloadLink() {
                    return QqMusic.baseDownloadServer + downloadLink;
                }

                public void setDownloadLink(String downloadLink) {
                    this.downloadLink = downloadLink;
                }
            }
        }
    }


}
