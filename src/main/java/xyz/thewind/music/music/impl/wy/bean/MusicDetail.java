package xyz.thewind.music.music.impl.wy.bean;


import xyz.thewind.music.music.impl.wy.bean.search.Album;
import xyz.thewind.music.music.impl.wy.bean.search.Singer;

import java.util.ArrayList;
import java.util.List;

public class MusicDetail {
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

    public static class Song {
        public String name;
        public long id;
        public int duration;
        public List<Singer> artists = new ArrayList<>();
        public Album album;

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

        public List<Singer> getArtists() {
            return artists;
        }

        public void setArtists(List<Singer> artists) {
            this.artists = artists;
        }

        public Album getAlbum() {
            return album;
        }

        public void setAlbum(Album album) {
            this.album = album;
        }

    }
}
