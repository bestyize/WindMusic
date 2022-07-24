package xyz.thewind.music.music.impl.qq.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SongDetail {
    int code;
    public Songinfo songinfo = new Songinfo();

    public  class Songinfo {
        int code;
        public Songinfo.Data data = new Songinfo.Data();

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Songinfo.Data getData() {
            return data;
        }

        public void setData(Songinfo.Data data) {
            this.data = data;
        }

        public  class Data {
            @SerializedName("track_info")
            public Songinfo.Data.TrackInfo trackInfo = new Songinfo.Data.TrackInfo();
            public List<Songinfo.Data.Info> info = new ArrayList<>();

            public List<Songinfo.Data.Info> getInfo() {
                return info;
            }

            public void setInfo(List<Songinfo.Data.Info> info) {
                this.info = info;
            }

            public Songinfo.Data.TrackInfo getTrackInfo() {
                return trackInfo;
            }

            public void setTrackInfo(Songinfo.Data.TrackInfo trackInfo) {
                this.trackInfo = trackInfo;
            }

            public  class TrackInfo {
                String mid;
                String name;
                List<Songinfo.Data.TrackInfo.Singer> singer = new ArrayList<>();
                Songinfo.Data.TrackInfo.Album album = new Songinfo.Data.TrackInfo.Album();
                Songinfo.Data.TrackInfo.Mv mv = new Songinfo.Data.TrackInfo.Mv();
                @SerializedName("interval")
                int duration;

                public String getMid() {
                    return mid;
                }

                public void setMid(String mid) {
                    this.mid = mid;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<Songinfo.Data.TrackInfo.Singer> getSinger() {
                    return singer;
                }

                public void setSinger(List<Songinfo.Data.TrackInfo.Singer> singer) {
                    this.singer = singer;
                }

                public Songinfo.Data.TrackInfo.Album getAlbum() {
                    return album;
                }

                public void setAlbum(Songinfo.Data.TrackInfo.Album album) {
                    this.album = album;
                }

                public Songinfo.Data.TrackInfo.Mv getMv() {
                    return mv;
                }

                public void setMv(Songinfo.Data.TrackInfo.Mv mv) {
                    this.mv = mv;
                }

                public int getDuration() {
                    return duration;
                }

                public void setDuration(int duration) {
                    this.duration = duration;
                }

                public  class Singer {
                    public String mid;
                    public String name;

                    public String getMid() {
                        return mid;
                    }

                    public void setMid(String mid) {
                        this.mid = mid;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }

                public  class Album {
                    public String mid;
                    public String name;

                    public String getMid() {
                        return mid;
                    }

                    public void setMid(String mid) {
                        this.mid = mid;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }

                public class Mv {
                    public String vid;

                    public String getVid() {
                        return vid;
                    }

                    public void setVid(String vid) {
                        this.vid = vid;
                    }
                }

            }

            public  class Info {
                public String title;
                public String type;
                public List<Songinfo.Data.Info.Content> content = new ArrayList<>();

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public List<Songinfo.Data.Info.Content> getContent() {
                    return content;
                }

                public void setContent(List<Songinfo.Data.Info.Content> content) {
                    this.content = content;
                }

                public  class Content {
                    public String value;
                    String mid;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public String getMid() {
                        return mid;
                    }

                    public void setMid(String mid) {
                        this.mid = mid;
                    }
                }
            }
        }
    }
}
