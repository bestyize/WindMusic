package xyz.thewind.music.music.impl.mg.bean.search;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.common.MusicCommonConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SongDetail {
    public String id;
    public String contentId;
    @SerializedName("copyrightId")
    public String songId;
    @SerializedName("name")
    public String songName;
    public List<Singer> singers = new ArrayList<>();
    public List<Album> albums = new ArrayList<>();
    public String lyricUrl;

    public List<Mv> mvList = new ArrayList<>();

    public List<ImgItem> imgItems;
    List<RateFormat> newRateFormats = new ArrayList<>();
    @SerializedName("z3dCode")
    public RateFormat eqRateFormat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
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

    public List<Singer> getSingers() {
        return singers;
    }

    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public String getLyricUrl() {
        return lyricUrl;
    }

    public void setLyricUrl(String lyricUrl) {
        this.lyricUrl = lyricUrl;
    }

    public List<ImgItem> getImgItems() {
        return imgItems;
    }

    public void setImgItems(List<ImgItem> imgItems) {
        this.imgItems = imgItems;
    }

    public List<RateFormat> getNewRateFormats() {
        return newRateFormats;
    }

    public void setNewRateFormats(List<RateFormat> newRateFormats) {
        this.newRateFormats = newRateFormats;
    }

    public RateFormat getEqRateFormat() {
        return eqRateFormat;
    }

    public void setEqRateFormat(RateFormat eqRateFormat) {
        this.eqRateFormat = eqRateFormat;
    }

    public List<String> getSingersId() {
        List<String> singerIdList = new LinkedList<>();
        for (Singer singer  : singers) {
            singerIdList.add(singer.id);
        }
        return singerIdList;
    }

    public List<String> getSingersName() {
        List<String> singerNameList = new LinkedList<>();
        for (Singer singer : singers) {
            singerNameList.add(singer.name);
        }
        return singerNameList;
    }

    public String getAlbumId() {
        if (albums.size() == 0) {
            return "";
        }
        return albums.get(0).getId();
    }

    public String getAlbumName() {
        if (albums.size() == 0) {
            return "";
        }
        return albums.get(0).getName();
    }

    public String getMvId() {
        String mvId = "";
        if (mvList.size() == 0) {
            return mvId;
        }
        return mvList.get(0).mvId;
    }


    public String getQuality() {
        if (eqRateFormat != null) {
            return MusicCommonConfig.QUALITY_EQ;
        }
        if (newRateFormats.size() >= 4) {
            return MusicCommonConfig.QUALITY_ZQ;
        }
        if (newRateFormats.size() >= 3) {
            return MusicCommonConfig.QUALITY_SQ;
        }
        if (newRateFormats.size() >= 2) {
            return MusicCommonConfig.QUALITY_HQ;
        }
        return MusicCommonConfig.QUALITY_PQ;
    }

    public String getPicUrl() {
        String picUrl = "";
        if (imgItems.size() == 0) {
            return picUrl;
        }
        return imgItems.get(0).imgUrl;
    }
}
