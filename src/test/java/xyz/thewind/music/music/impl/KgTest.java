package xyz.thewind.music.music.impl;

import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.music.impl.kg.KgMusic;
import xyz.thewind.music.util.GsonConverter;

import java.util.List;

public class KgTest {
    @Test
    public void testKg(){
        MusicHelper helper=new KgMusic();
        List<SongBean> songBeanList=helper.searchWithDownload("双笙",10);
        System.out.println(GsonConverter.toJson(songBeanList));
    }
}
