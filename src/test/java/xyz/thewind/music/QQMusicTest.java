package xyz.thewind.music;

import org.junit.Test;

import xyz.thewind.music.music.impl.qq.module.RequestModule;

public class QQMusicTest {
    @Test
    public void testEnc(){

        for (int i=0;i<1000;i++){
            System.out.println(i);
            System.out.println(RequestModule.getSongDetail("003tzjXT4g0LPK"));
            System.out.println(RequestModule.getHotSingerList());
        }

    }
}
