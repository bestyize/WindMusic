package xyz.thewind.music.music.impl.wy.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponseBean {
    public boolean needLogin;
    public Data result;
    public int code;

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public Data getResult() {
        return result;
    }

    public void setResult(Data result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public class Data {
        @SerializedName("songs")
        public List<Data.SongDetail> songList;
        public int songCount;

        public List<Data.SongDetail> getSongsList() {
            return songList;
        }

        public void setSongsList(List<Data.SongDetail> songsList) {
            this.songList = songsList;
        }

        public int getSongCount() {
            return songCount;
        }

        public void setSongCount(int songCount) {
            this.songCount = songCount;
        }

        public class SongDetail {
            @SerializedName("id")
            long songId;
            @SerializedName("name")
            String songName;
            List<Data.SongDetail.Singer> ar;
            Data.SongDetail.Album al;

            @SerializedName("sq")
            Data.SongDetail.Quality sQuality;
            @SerializedName("h")
            Data.SongDetail.Quality hQuality;
            @SerializedName("p")
            Data.SongDetail.Quality pQuality;
            @SerializedName("l")
            Data.SongDetail.Quality lQuality;

            @SerializedName("hr")
            Data.SongDetail.Quality zqQuality;
            @SerializedName("mv")
            long mvId;

            Data.SongDetail.Privilege privilege;

            public long getSongId() {
                return songId;
            }

            public void setSongId(long songId) {
                this.songId = songId;
            }

            public String getSongName() {
                return songName;
            }

            public void setSongName(String songName) {
                this.songName = songName;
            }

            public List<Data.SongDetail.Singer> getAr() {
                return ar;
            }

            public void setAr(List<Data.SongDetail.Singer> ar) {
                this.ar = ar;
            }

            public Data.SongDetail.Album getAl() {
                return al;
            }

            public void setAl(Data.SongDetail.Album al) {
                this.al = al;
            }

            public Data.SongDetail.Quality getsQuality() {
                return sQuality;
            }

            public void setsQuality(Data.SongDetail.Quality sQuality) {
                this.sQuality = sQuality;
            }

            public Data.SongDetail.Quality gethQuality() {
                return hQuality;
            }

            public void sethQuality(Data.SongDetail.Quality hQuality) {
                this.hQuality = hQuality;
            }

            public Data.SongDetail.Quality getpQuality() {
                return pQuality;
            }

            public void setpQuality(Data.SongDetail.Quality pQuality) {
                this.pQuality = pQuality;
            }

            public Data.SongDetail.Quality getlQuality() {
                return lQuality;
            }

            public void setlQuality(Data.SongDetail.Quality lQuality) {
                this.lQuality = lQuality;
            }

            public Quality getZqQuality() {
                return zqQuality;
            }

            public void setZqQuality(Quality zqQuality) {
                this.zqQuality = zqQuality;
            }

            public long getMvId() {
                return mvId;
            }

            public void setMvId(long mvId) {
                this.mvId = mvId;
            }

            public Data.SongDetail.Privilege getPrivilege() {
                return privilege;
            }

            public void setPrivilege(Data.SongDetail.Privilege privilege) {
                this.privilege = privilege;
            }

            public class Singer {
                @SerializedName("id")
                long singerId;
                @SerializedName("name")
                String singerName;

                public long getSingerId() {
                    return singerId;
                }

                public void setSingerId(long singerId) {
                    this.singerId = singerId;
                }

                public String getSingerName() {
                    return singerName;
                }

                public void setSingerName(String singerName) {
                    this.singerName = singerName;
                }
            }

            public class Album {
                @SerializedName("id")
                long albumId;
                @SerializedName("name")
                String albumName;
                @SerializedName("picUrl")
                String albumPicUrl;

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

            public class Quality {
                @SerializedName("br")
                String bitRate;
                @SerializedName("size")
                long fileSize;

                public String getBitRate() {
                    return bitRate;
                }

                public void setBitRate(String bitRate) {
                    this.bitRate = bitRate;
                }

                public long getFileSize() {
                    return fileSize;
                }

                public void setFileSize(long fileSize) {
                    this.fileSize = fileSize;
                }
            }

            public class Privilege {
                public int maxbr;

                public int getMaxbr() {
                    return maxbr;
                }

                public void setMaxbr(int maxbr) {
                    this.maxbr = maxbr;
                }
            }

        }
    }
}
