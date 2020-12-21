package xyz.thewind.music.music.impl.mg.bean;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.impl.mg.MgMusic;

import java.util.ArrayList;
import java.util.List;

public class MgDownloadBean {
    String code;
    String info;
    List<MgMusicDetail> resource = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<MgMusicDetail> getResource() {
        return resource;
    }

    public void setResource(List<MgMusicDetail> resource) {
        this.resource = resource;
    }

    public class MgMusicDetail {
        String songName;
        String singerId;
        @SerializedName("singer")
        String singerName;
        @SerializedName("album")
        String albumName;
        String albumId;
        List<MgMusicDetail.Imgs> albumImgs = new ArrayList<>();
        String lrcUrl;
        @SerializedName("newRateFormats")
        List<MgMusicDetail.RateFormats> rateFormats;

        public String getSongName() {
            return songName;
        }

        public void setSongName(String songName) {
            this.songName = songName;
        }

        public String getSingerId() {
            return singerId;
        }

        public void setSingerId(String singerId) {
            this.singerId = singerId;
        }

        public String getSingerName() {
            return singerName;
        }

        public void setSingerName(String singerName) {
            this.singerName = singerName;
        }

        public String getAlbumName() {
            return albumName;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        public String getAlbumId() {
            return albumId;
        }

        public void setAlbumId(String albumId) {
            this.albumId = albumId;
        }

        public List<MgMusicDetail.Imgs> getAlbumImgs() {
            return albumImgs;
        }

        public void setAlbumImgs(List<MgMusicDetail.Imgs> albumImgs) {
            this.albumImgs = albumImgs;
        }

        public String getLrcUrl() {
            return lrcUrl;
        }

        public void setLrcUrl(String lrcUrl) {
            this.lrcUrl = lrcUrl;
        }

        public List<MgMusicDetail.RateFormats> getRateFormats() {
            return rateFormats;
        }

        public void setRateFormats(List<MgMusicDetail.RateFormats> rateFormats) {
            this.rateFormats = rateFormats;
        }

        public class RateFormats {
            String formatType;
            @SerializedName(value = "url", alternate = "androidUrl")
            String url;
            String size;

            public String getFormatType() {
                return formatType;
            }

            public void setFormatType(String formatType) {
                this.formatType = formatType;
            }

            public String getUrl() {
                if (url != null && url.contains("/public")) {
                    url = "http://freetyst.nf.migu.cn" + url.substring(url.indexOf("/public"));
                }
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }
        }

        public class Imgs {
            String imgSizeType;
            String img;

            public String getImgSizeType() {
                return imgSizeType;
            }

            public void setImgSizeType(String imgSizeType) {
                this.imgSizeType = imgSizeType;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }
        }

    }

}
