package xyz.thewind.music.music.impl;

import org.junit.jupiter.api.Test;

import java.io.File;

import static xyz.thewind.music.music.impl.qq.enc.QQMusicDecrypt.decrypt;

public class MusicFileDecTest {
    @Test
    public void test()throws Exception{
        File file=new File("E:\\QQMusicCache\\QMDL");
        File[] files=file.listFiles();
        for (File f:files){
            System.out.println(decrypt(f.getAbsolutePath()));
        }
        System.out.println(decrypt("E:\\QQMusicCache\\QMDL\\F0M0001pkF2E0nAO6P.mflac.cache"));
    }
}
