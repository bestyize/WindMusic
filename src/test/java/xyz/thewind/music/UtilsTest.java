package xyz.thewind.music;

import org.junit.Test;
import xyz.thewind.music.auth.TokenHelper;

public class UtilsTest {
    @Test
    public void testGetToken(){
        String cookie= TokenHelper.generateToken("bestyize","zr2014");
        System.out.println(cookie);
    }
}
