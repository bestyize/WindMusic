package xyz.thewind.music.music.impl.wy.bean;


import java.util.ArrayList;
import java.util.List;

public class SongDetail {
    int code;
    List<Song> songs = new ArrayList<>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public class Song {
        String name;
        long id;
        int duration;
        List<Song.Artist> artists = new ArrayList<>();
        Song.Album album;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public List<Song.Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Song.Artist> artists) {
            this.artists = artists;
        }

        public Song.Album getAlbum() {
            return album;
        }

        public void setAlbum(Song.Album album) {
            this.album = album;
        }

        public class Artist {
            String name;
            long id;
            String picUrl;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }
        }

        public class Album {
            String name;
            long id;
            String picUrl;
            long publishTime;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public long getPublishTime() {
                return publishTime;
            }

            public void setPublishTime(long publishTime) {
                this.publishTime = publishTime;
            }
        }
    }
}
