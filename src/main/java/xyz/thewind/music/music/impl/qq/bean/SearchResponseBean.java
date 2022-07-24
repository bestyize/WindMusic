package xyz.thewind.music.music.impl.qq.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponseBean {
    int code;
    public Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public  class Data {
        String keyword;
        public Data.Song song;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public Data.Song getSong() {
            return song;
        }

        public void setSong(Data.Song song) {
            this.song = song;
        }

        public  class Song {
            @SerializedName("curnum")
            int curNum;
            @SerializedName("curpage")
            int curPage;
            @SerializedName("list")
            public List<Data.Song.SongDetail> songDetailList;

            public int getCurNum() {
                return curNum;
            }

            public void setCurNum(int curNum) {
                this.curNum = curNum;
            }

            public int getCurPage() {
                return curPage;
            }

            public void setCurPage(int curPage) {
                this.curPage = curPage;
            }

            public List<Data.Song.SongDetail> getSongDetailList() {
                return songDetailList;
            }

            public void setSongDetailList(List<Data.Song.SongDetail> songDetailList) {
                this.songDetailList = songDetailList;
            }

            public  class SongDetail {
                @SerializedName("songmid")
                String songId;
                @SerializedName("songname")
                String songName;
                @SerializedName("vid")
                String mvId;
                @SerializedName("albummid")
                String albumId;
                @SerializedName("albumname")
                String albumName;
                @SerializedName("singer")
                List<Data.Song.SongDetail.Singer> singers;
                @SerializedName("sizeogg")
                long lqSize;
                @SerializedName("size128")
                long pqSize;
                @SerializedName("size320")
                long hqSize;
                @SerializedName("sizeflac")
                long sqSize;
                @SerializedName("interval")
                long duration;

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

                public String getMvId() {
                    return mvId;
                }

                public void setMvId(String mvId) {
                    this.mvId = mvId;
                }

                public String getAlbumId() {
                    return albumId;
                }

                public void setAlbumId(String albumId) {
                    this.albumId = albumId;
                }

                public String getAlbumName() {
                    return albumName;
                }

                public void setAlbumName(String albumName) {
                    this.albumName = albumName;
                }

                public List<Data.Song.SongDetail.Singer> getSingers() {
                    return singers;
                }

                public void setSingers(List<Data.Song.SongDetail.Singer> singers) {
                    this.singers = singers;
                }

                public long getLqSize() {
                    return lqSize;
                }

                public void setLqSize(long lqSize) {
                    this.lqSize = lqSize;
                }

                public long getPqSize() {
                    return pqSize;
                }

                public void setPqSize(long pqSize) {
                    this.pqSize = pqSize;
                }

                public long getHqSize() {
                    return hqSize;
                }

                public void setHqSize(long hqSize) {
                    this.hqSize = hqSize;
                }

                public long getSqSize() {
                    return sqSize;
                }

                public void setSqSize(long sqSize) {
                    this.sqSize = sqSize;
                }

                public long getDuration() {
                    return duration;
                }

                public void setDuration(long duration) {
                    this.duration = duration;
                }

                public class Singer {
                    @SerializedName("mid")
                    String singerId;
                    @SerializedName("name")
                    String singerName;

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
                }


            }
        }
    }
}
