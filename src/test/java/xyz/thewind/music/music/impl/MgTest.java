package xyz.thewind.music.music.impl;

import org.junit.Test;
import xyz.thewind.music.music.impl.mg.MgMusicModel;
import xyz.thewind.music.util.GsonUtil;

public class MgTest {

    @Test
    public void testMgEnc() {
        //System.out.println(MgEncrypt.searchMusic("周杰伦", 1, 5));
        System.out.println(GsonUtil.toJson(new MgMusicModel().searchMusic("周杰伦",1, 1)));
    }

}
