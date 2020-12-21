package xyz.thewind.music.music.impl.xm.bean;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.impl.xm.XmMusic;

import java.util.HashMap;
import java.util.List;

public class SearchResponseBean {
    String api;
    Data1 data;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Data1 getData() {
        return data;
    }

    public void setData(Data1 data) {
        this.data = data;
    }

    public class Data1 {
        Data1.Data data;

        public Data1.Data getData() {
            return data;
        }

        public void setData(Data1.Data data) {
            this.data = data;
        }

        public class Data {
            @SerializedName("songs")
            List<Data1.Data.SongDetail> songDetailList;

            public List<SongDetail> getSongDetailList() {
                return songDetailList;
            }

            public void setSongDetailList(List<SongDetail> songDetailList) {
                this.songDetailList = songDetailList;
            }

            public class SongDetail {
                String songId;
                String songName;
                long albumId;
                @SerializedName("albumLogo")
                String albumPicUrl;
                String albumName;
                int albumSongCount;
                @SerializedName("artistId")
                long singerId;
                @SerializedName("artistLogo")
                String singerPic;
                List<Data1.Data.SongDetail.ListenFiles> listenFiles;
                Data1.Data.SongDetail.LyricInfo lyricInfo;
                String mvId;
                @SerializedName("singerVOs")
                List<Data1.Data.SongDetail.SingerVOs> singerList;

                public class SingerVOs {
                    @SerializedName("artistStringId")
                    String singerId;
                    @SerializedName("artistLogo")
                    String singerPic;
                    @SerializedName("artistName")
                    String singerName;
                    String gender;

                    public String getSingerId() {
                        return singerId;
                    }

                    public void setSingerId(String singerId) {
                        this.singerId = singerId;
                    }

                    public String getSingerPic() {
                        return singerPic;
                    }

                    public void setSingerPic(String singerPic) {
                        this.singerPic = singerPic;
                    }

                    public String getSingerName() {
                        return singerName;
                    }

                    public void setSingerName(String singerName) {
                        this.singerName = singerName;
                    }

                    public String getGender() {
                        return gender;
                    }

                    public void setGender(String gender) {
                        this.gender = gender;
                    }
                }

                public class ListenFiles {

                    long fileSize;
                    String format;
                    @SerializedName("length")
                    long timeSpan;
                    @SerializedName("listenFile")
                    String downloadLink;
                    String quality;

                    public long getFileSize() {
                        return fileSize;
                    }

                    public void setFileSize(long fileSize) {
                        this.fileSize = fileSize;
                    }

                    public String getFormat() {
                        return format;
                    }

                    public void setFormat(String format) {
                        this.format = format;
                    }

                    public long getTimeSpan() {
                        return timeSpan;
                    }

                    public void setTimeSpan(long timeSpan) {
                        this.timeSpan = timeSpan;
                    }

                    public String getDownloadLink() {
                        return downloadLink;
                    }

                    public void setDownloadLink(String downloadLink) {
                        this.downloadLink = downloadLink;
                    }

                    public String getQuality() {
                        if (XmMusic.qualityMap == null) {
                            XmMusic.qualityMap = new HashMap<>();
                            XmMusic.qualityMap.put("e", "LQ");
                            XmMusic.qualityMap.put("f", "LQ");
                            XmMusic.qualityMap.put("l", "PQ");
                            XmMusic.qualityMap.put("h", "HQ");
                            XmMusic.qualityMap.put("s", "SQ");
                        }
                        return XmMusic.qualityMap.get(quality);
                    }

                    public void setQuality(String quality) {
                        this.quality = quality;
                    }
                }

                public String getSongId() {
                    return songId;
                }

                public void setSongId(String songId) {
                    this.songId = songId;
                }

                public String getSongName() {
                    return songName;
                }

                public void setSongName(String songName) {
                    this.songName = songName;
                }

                public long getAlbumId() {
                    return albumId;
                }

                public void setAlbumId(long albumId) {
                    this.albumId = albumId;
                }

                public String getAlbumPicUrl() {
                    return albumPicUrl;
                }

                public void setAlbumPicUrl(String albumPicUrl) {
                    this.albumPicUrl = albumPicUrl;
                }

                public String getAlbumName() {
                    return albumName;
                }

                public void setAlbumName(String albumName) {
                    this.albumName = albumName;
                }

                public int getAlbumSongCount() {
                    return albumSongCount;
                }

                public void setAlbumSongCount(int albumSongCount) {
                    this.albumSongCount = albumSongCount;
                }

                public long getSingerId() {
                    return singerId;
                }

                public void setSingerId(long singerId) {
                    this.singerId = singerId;
                }

                public String getSingerPic() {
                    return singerPic;
                }

                public void setSingerPic(String singerPic) {
                    this.singerPic = singerPic;
                }

                public List<Data1.Data.SongDetail.ListenFiles> getListenFiles() {
                    return listenFiles;
                }

                public void setListenFiles(List<Data1.Data.SongDetail.ListenFiles> listenFiles) {
                    this.listenFiles = listenFiles;
                }

                public Data1.Data.SongDetail.LyricInfo getLyricInfo() {
                    return lyricInfo;
                }

                public void setLyricInfo(Data1.Data.SongDetail.LyricInfo lyricInfo) {
                    this.lyricInfo = lyricInfo;
                }

                public String getMvId() {
                    return mvId;
                }

                public void setMvId(String mvId) {
                    this.mvId = mvId;
                }

                public List<Data1.Data.SongDetail.SingerVOs> getSingerList() {
                    return singerList;
                }

                public void setSingerList(List<Data1.Data.SongDetail.SingerVOs> singerList) {
                    this.singerList = singerList;
                }

                public class LyricInfo {
                    @SerializedName("lyricFile")
                    String lyricUrl;
                    String lyricId;

                    public String getLyricUrl() {
                        return lyricUrl;
                    }

                    public void setLyricUrl(String lyricUrl) {
                        this.lyricUrl = lyricUrl;
                    }

                    public String getLyricId() {
                        return lyricId;
                    }

                    public void setLyricId(String lyricId) {
                        this.lyricId = lyricId;
                    }
                }

            }
        }

    }
}
