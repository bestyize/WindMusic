package xyz.thewind.music.music.impl.kg.module;

import xyz.thewind.music.music.impl.qq.module.ThirdPartApi;

public class RequestModule {
    public static String getPlayUrl(String songId,String quality){
        return ThirdPartApi.getPlayUrl(songId,quality);
    }
}
