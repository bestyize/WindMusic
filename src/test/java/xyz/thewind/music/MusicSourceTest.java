package xyz.thewind.music;

import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicDispatcher;
import xyz.thewind.music.util.GsonConverter;

import java.util.List;

public class MusicSourceTest {
    @Test
    public void test(){
        MusicDispatcher dispatcher=new MusicDispatcher();
        List<SongBean> songBeanList=dispatcher.searchDownDispatcher("MG","双笙",5);
        System.out.println(GsonConverter.toJson(songBeanList));
    }
}
