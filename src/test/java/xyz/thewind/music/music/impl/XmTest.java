package xyz.thewind.music.music.impl;

import org.junit.Test;
import xyz.thewind.music.music.impl.xm.module.RequestModule;

public class XmTest {
    @Test
    public void testSearch(){
        System.out.println(RequestModule.searchSong("五月天",0,10));
    }
}
