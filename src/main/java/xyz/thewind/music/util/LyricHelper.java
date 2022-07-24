package xyz.thewind.music.util;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;

public class LyricHelper {
    public static String getSingerName(String songSrc,String lyricStr){
        int line=1;
        if(songSrc.equals(SRC_KG)){
            line++;
        }
        return fetchLine(lyricStr,line);
    }

    public static String getSongName(String songSrc,String lyricStr){
        int line=0;
        if(songSrc.equals(SRC_KG)){
            line++;
        }
        return fetchLine(lyricStr,line);
    }

    public static String fetchLine(String lyricStr,int line){
        lyricStr=lyricStr.replaceAll("\r\n","\n").replaceAll("\\[","");
        String[] lyrics= lyricStr.split("\n");
        String dat="";
        if(line<=lyrics.length-1){
            dat=lyrics[line];
            if(dat.startsWith("ti:")){
                dat=dat.replace("ti:","").replace("]","");
            }else if(dat.startsWith("ar:")){
                dat=dat.replace("ar:","").replace("]","");
            }else {
                dat=dat.substring(dat.indexOf("]")+1);
            }
        }
        return dat;
    }
}
