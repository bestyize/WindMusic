package xyz.thewind.music.music.impl.qq.enc;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QQMusicDecrypt {
    private static int[] qmcEncMap = {0x77, 0x48, 0x32, 0x73, 0xDE, 0xF2, 0xC0, 0xC8, 0x95, 0xEC, 0x30, 0xB2, 0x51, 0xC3, 0xE1, 0xA0, 0x9E, 0xE6, 0x9D, 0xCF, 0xFA, 0x7F, 0x14, 0xD1, 0xCE, 0xB8, 0xDC, 0xC3, 0x4A, 0x67, 0x93, 0xD6, 0x28, 0xC2, 0x91, 0x70, 0xCA, 0x8D, 0xA2, 0xA4, 0xF0, 8, 0x61, 0x90, 0x7E, 0x6F, 0xA2, 0xE0, 0xEB, 0xAE, 0x3E, 0xB6, 0x67, 0xC7, 0x92, 0xF4, 0x91, 0xB5, 0xF6, 0x6C, 0x5E, 0x84, 0x40, 0xF7, 0xF3, 0x1B, 2, 0x7F, 0xD5, 0xAB, 0x41, 0x89, 0x28, 0xF4, 0x25, 0xCC, 0x52, 0x11, 0xAD, 0x43, 0x68, 0xA6, 0x41, 0x8B, 0x84, 0xB5, 0xFF, 0x2C, 0x92, 0x4A, 0x26, 0xD8, 0x47, 0x6A, 0x7C, 0x95, 0x61, 0xCC, 0xE6, 0xCB, 0xBB, 0x3F, 0x47, 0x58, 0x89, 0x75, 0xC3, 0x75, 0xA1, 0xD9, 0xAF, 0xCC, 8, 0x73, 0x17, 0xDC, 0xAA, 0x9A, 0xA2, 0x16, 0x41, 0xD8, 0xA2, 6, 0xC6, 0x8B, 0xFC, 0x66, 0x34, 0x9F, 0xCF, 0x18, 0x23, 0xA0, 0xA, 0x74, 0xE7, 0x2B, 0x27, 0x70, 0x92, 0xE9, 0xAF, 0x37, 0xE6, 0x8C, 0xA7, 0xBC, 0x62, 0x65, 0x9C, 0xC2, 8, 0xC9, 0x88, 0xB3, 0xF3, 0x43, 0xAC, 0x74, 0x2C, 0xF, 0xD4, 0xAF, 0xA1, 0xC3, 1, 0x64, 0x95, 0x4E, 0x48, 0x9F, 0xF4, 0x35, 0x78, 0x95, 0x7A, 0x39, 0xD6, 0x6A, 0xA0, 0x6D, 0x40, 0xE8, 0x4F, 0xA8, 0xEF, 0x11, 0x1D, 0xF3, 0x1B, 0x3F, 0x3F, 7, 0xDD, 0x6F, 0x5B, 0x19, 0x30, 0x19, 0xFB, 0xEF, 0xE, 0x37, 0xF0, 0xE, 0xCD, 0x16, 0x49, 0xFE, 0x53, 0x47, 0x13, 0x1A, 0xBD, 0xA4, 0xF1, 0x40, 0x19, 0x60, 0xE, 0xED, 0x68, 9, 6, 0x5F, 0x4D, 0xCF, 0x3D, 0x1A, 0xFE, 0x20, 0x77, 0xE4, 0xD9, 0xDA, 0xF9, 0xA4, 0x2B, 0x76, 0x1C, 0x71, 0xDB, 0, 0xBC, 0xFD, 0xC, 0x6C, 0xA5, 0x47, 0xF7, 0xF6, 0, 0x79, 0x4A, 0x11};
    private static int[] pcCacheEncMap = {0xD3, 0xD7, 0xDB, 0xDF, 0xC3, 0xC7, 0xCB, 0xCF, 0xF3, 0xF7, 0xFB, 0xFF, 0xE3, 0xE7, 0xEB, 0xEF, 0x93, 0x97, 0x9B, 0x9F, 0x83, 0x87, 0x8B, 0x8F, 0xB3, 0xB7, 0xBB, 0xBF, 0xA3, 0xA7, 0xAB, 0xAF, 0x53, 0x57, 0x5B, 0x5F, 0x43, 0x47, 0x4B, 0x4F, 0x73, 0x77, 0x7B, 0x7F, 0x63, 0x67, 0x6B, 0x6F, 0x13, 0x17, 0x1B, 0x1F, 0x3, 0x7, 0xB, 0xF, 0x33, 0x37, 0x3B, 0x3F, 0x23, 0x27, 0x2B, 0x2F, 0xD2, 0xD6, 0xDA, 0xDE, 0xC2, 0xC6, 0xCA, 0xCE, 0xF2, 0xF6, 0xFA, 0xFE, 0xE2, 0xE6, 0xEA, 0xEE, 0x92, 0x96, 0x9A, 0x9E, 0x82, 0x86, 0x8A, 0x8E, 0xB2, 0xB6, 0xBA, 0xBE, 0xA2, 0xA6, 0xAA, 0xAE, 0x52, 0x56, 0x5A, 0x5E, 0x42, 0x46, 0x4A, 0x4E, 0x72, 0x76, 0x7A, 0x7E, 0x62, 0x66, 0x6A, 0x6E, 0x12, 0x16, 0x1A, 0x1E, 0x2, 0x6, 0xA, 0xE, 0x32, 0x36, 0x3A, 0x3E, 0x22, 0x26, 0x2A, 0x2E, 0xD1, 0xD5, 0xD9, 0xDD, 0xC1, 0xC5, 0xC9, 0xCD, 0xF1, 0xF5, 0xF9, 0xFD, 0xE1, 0xE5, 0xE9, 0xED, 0x91, 0x95, 0x99, 0x9D, 0x81, 0x85, 0x89, 0x8D, 0xB1, 0xB5, 0xB9, 0xBD, 0xA1, 0xA5, 0xA9, 0xAD, 0x51, 0x55, 0x59, 0x5D, 0x41, 0x45, 0x49, 0x4D, 0x71, 0x75, 0x79, 0x7D, 0x61, 0x65, 0x69, 0x6D, 0x11, 0x15, 0x19, 0x1D, 0x1, 0x5, 0x9, 0xD, 0x31, 0x35, 0x39, 0x3D, 0x21, 0x25, 0x29, 0x2D, 0xD0, 0xD4, 0xD8, 0xDC, 0xC0, 0xC4, 0xC8, 0xCC, 0xF0, 0xF4, 0xF8, 0xFC, 0xE0, 0xE4, 0xE8, 0xEC, 0x90, 0x94, 0x98, 0x9C, 0x80, 0x84, 0x88, 0x8C, 0xB0, 0xB4, 0xB8, 0xBC, 0xA0, 0xA4, 0xA8, 0xAC, 0x50, 0x54, 0x58, 0x5C, 0x40, 0x44, 0x48, 0x4C, 0x70, 0x74, 0x78, 0x7C, 0x60, 0x64, 0x68, 0x6C, 0x10, 0x14, 0x18, 0x1C, 0x0, 0x4, 0x8, 0xC, 0x30, 0x34, 0x38, 0x3C, 0x20, 0x24, 0x28, 0x2C};
    private static int[] posMap ={27 ,28 ,31 ,36 ,43 ,52 ,63 ,76 ,91 ,108,127,148,171,196,223,252,27 ,60 ,95 ,132,171,212,255,44 ,91 ,140,191,244,43 ,100,159,220, 27 ,92 ,159,228,43 ,116,191,12 ,91 ,172,255,84 ,171,4  ,95 ,188, 27 ,124,223,68 ,171,20 ,127,236,91 ,204,63 ,180,43 ,164,31 ,156,27,156,31,164,43,180,63 ,204,91 ,236,127,20,171,68,223,124,27,188,95,4 ,171,84 ,255,172,91 ,12 ,191,116,43 ,228,159,92 ,27 ,220,159,100,43 ,244,191,140,91 ,44 ,255,212,171,132,95 ,60 ,27 ,252,223,196,171,148,127,108,91 ,76 ,63 ,52 ,43 ,36 ,31 ,28 };
//    @Test
//    public void test()throws Exception{
//        File file=new File("E:\\QQMusicCache\\QMDL");
//        File[] files=file.listFiles();
//        for (File f:files){
//            System.out.println(decrypt(f.getAbsolutePath()));
//        }
//    }

    public static boolean decrypt(@NotNull String fullPath){
        try {
            return decrypt(fullPath,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean decrypt(@NotNull String fullPath,boolean deleteOrigin) throws Exception{
        if(decryptDispatcher(fullPath)){
            if(deleteOrigin){
                new File(fullPath).delete();
            }
            return renameMusicFile(getDecryptName(fullPath));
        }
        return false;
    }

    private static boolean decryptDispatcher(@NotNull String fullPath)throws Exception{
        if (fullPath.endsWith(".flac.cache") || fullPath.endsWith(".mp3.cache")) {//PC端QQ音乐缓存文件
            return decryptCacheFile(fullPath);
        }else if(fullPath.endsWith(".qmcflac")||fullPath.endsWith(".qmc0")||fullPath.endsWith(".qmc2")||fullPath.endsWith("qmc3")||fullPath.endsWith(".bkcflac") || fullPath.endsWith(".bkcmp3")){
            return decryptBkcQmcFile(fullPath);
        }else if(fullPath.endsWith(".flac.mqcc")||fullPath.endsWith(".mp3.mqcc")){
            return decryptMqccFile(fullPath);
        }else if (fullPath.endsWith(".flac.efe") || fullPath.endsWith(".mp3.efe")){
            return decryptEfeFile(fullPath);
        }else if(fullPath.endsWith(".mflac")||fullPath.endsWith(".mgg")){//未实现
            return decryptMFile(fullPath);
        }else if (fullPath.endsWith(".mflac.cache")||fullPath.endsWith(".mgg.cache")){
            return decryptMCacheFile(fullPath);
        }
       return false;
    }

    /**
     * bkc\qmc文件解密
     * @param fullPath
     * @return
     * @throws Exception
     */
    private static boolean decryptBkcQmcFile(String fullPath) throws Exception{
        File file=new File(fullPath);
        FileInputStream is=new FileInputStream(file);
        FileOutputStream fout=new FileOutputStream(getDecryptName(fullPath));
        byte[] b=new byte[8192];
        int len=0;
        long total=0;
        while ((len=is.read(b))!=-1){
            for (int i=0;i<len;i++){
                int pos=(int)((total+i)%0x7fff);
                b[i]^=qmcEncMap[(pos*pos+80923)%256];
            }
            fout.write(b,0,len);
            total+=len;
        }
        return false;
    }

    /**
     * PC端音乐文件cache解密，注意.mflac.cache文件和.mgg.cache文件需要二次解密
     *
     * @param fullPath
     * @return
     * @throws Exception
     */
    private static boolean decryptCacheFile(String fullPath) throws Exception {
        File file = new File(fullPath);
        FileInputStream is = new FileInputStream(file);
        FileOutputStream fout = new FileOutputStream(getDecryptName(fullPath));
        byte[] b = new byte[8192];
        int len = 0;
        while ((len = is.read(b)) != -1) {
            for (int i = 0; i < len; i++) {
                b[i] = (byte) pcCacheEncMap[0xff & b[i]];
            }
            fout.write(b, 0, len);
        }
        is.close();
        fout.close();
        return true;
    }

    private static boolean decryptMCacheFile(String fullPath) throws Exception{
        if(decryptCacheFile(fullPath)){
            return decryptMFile(getDecryptName(fullPath));
        }
        return false;
    }

    /**
     * 暂时没有实现
     * @param fullPath
     * @return
     */
    private static boolean decryptMFile(String fullPath){
//        int[] mKey= Arrays.copyOfRange(qmcEncMap,0,qmcEncMap.length);
        int[] mKey=new int[256];
        byte[] priKey=readBytes(fullPath,0x500,64);

        for (int i=0;i<64;i++){
            mKey[posMap[i]]=priKey[i];
        }
        return false;
    }

    /**
     * Android缓存的加密文件
     * @param fullPath
     * @return
     * @throws Exception
     */
    private static boolean decryptEfeFile(String fullPath)throws Exception{
        byte[] key=readBytes(fullPath,0x500,32);
        File file=new File(fullPath);
        FileInputStream is=new FileInputStream(file);
        File decryptFile=new File(getDecryptName(fullPath));
        FileOutputStream fout=new FileOutputStream(decryptFile);
        byte[] b=new byte[204800];
        int keyPos=0;
        int mod=0;
        int len=0;
        int curr=0;
        while ((len=is.read(b))!=-1){
            for(int i=0;i<len&&curr<0x500000;i++){
                if(mod==0){
                    b[i]^=key[keyPos];
                }
                int temp=keyPos+1;
                if(temp==key.length){
                    mod=(mod+1)%2;
                    keyPos=0;
                }else {
                    keyPos=temp;
                }
            }
            fout.write(b,0,len);
            curr+=len;
        }
        is.close();
        fout.close();
        return true;
    }

    /**
     * Android端缓存的不加密文件
     * @param fullPath
     * @return
     * @throws Exception
     */
    private static boolean decryptMqccFile(String fullPath){
        File decryptFile=new File(fullPath);
        return decryptFile.renameTo(new File(getDecryptName(fullPath)));
    }

    /**
     * 根据文件扩展名判断解密后应该的名字
     * @param fullPath
     * @return
     */
    private static String getDecryptName(@NotNull String fullPath) {
        if (fullPath.endsWith(".flac.cache") || fullPath.endsWith(".mp3.cache")||fullPath.endsWith(".mflac.cache")||fullPath.endsWith(".mgg.cache")) {//PC端QQ音乐缓存文件
            return fullPath.replace(".cache", "");
        } else if (fullPath.endsWith(".flac.efe") || fullPath.endsWith(".mp3.efe")||fullPath.endsWith(".flac.mqcc")||fullPath.endsWith(".mp3.mqcc")) {//Android端缓存文件
            return fullPath.substring(0,fullPath.lastIndexOf("."));
        } else if (fullPath.endsWith(".bkcflac") || fullPath.endsWith(".bkcmp3")) {//Moo音乐下载文件
            return fullPath.replace(".bkc", ".");
        }else if(fullPath.endsWith(".qmcflac")||fullPath.endsWith(".qmc0")||fullPath.endsWith(".qmc2")||fullPath.endsWith("qmc3")){
            if(fullPath.endsWith(".qmcflac")){
                return fullPath.replace(".qmc",".");
            }
            return fullPath.substring(0,fullPath.lastIndexOf(".")+1)+"mp3";
        }else if(fullPath.endsWith(".mflac")||fullPath.endsWith(".mgg")){
            if(fullPath.endsWith(".mflac")){
                return fullPath.replace(".mflac",".flac");
            }
            return fullPath.replace(".mgg",".mp3");
        }
        return "";
    }

    /**
     * 读取某一段数据
     * @param encryptFilePath
     * @param start
     * @param size
     * @return
     */
    private static byte[] readBytes(String encryptFilePath, long start, int size) {
        byte[] key = new byte[size];
        try {
            RandomAccessFile raf = new RandomAccessFile(encryptFilePath, "r");
            raf.seek(start);
            raf.readFully(key);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * 读取歌曲的元数据,获得歌手名+歌曲名
     * @param fullPath
     * @return
     */
    private static String getCorrectMusicFileName(@NotNull String fullPath){
        String dir=fullPath.substring(0,fullPath.lastIndexOf(File.separator)+1);
        Map<String,String> metaMap=readMetaInfo(fullPath);
        String newName=metaMap.get("singerName")+" - "+metaMap.get("songName")+fullPath.substring(fullPath.lastIndexOf("."));
        return dir+newName;
    }

    /**
     * 读取元数据
     * @param fullPath
     * @return
     */
    private static Map<String,String> readMetaInfo(@NotNull String fullPath){
        Map<String,String> map=new HashMap<>();
        byte[] bytes=readBytes(fullPath,0,1024);//元数据在开始的1k内
        map.put("songName",extraField(bytes,"TITLE"));
        map.put("singerName",extraField(bytes,"ARTIST"));
        map.put("album",extraField(bytes,"ALBUM"));
        return map;
    }

    /**
     * 提取音乐文件的字段
     * @param bytes
     * @param field
     * @return
     */
    private static String extraField(byte[] bytes,String field){
        for (int i=0;i<bytes.length;i++){
            if(field.length()>2&&(i+2)<bytes.length&&bytes.length>field.length()&&bytes[i]==field.charAt(0)&&bytes[i+1]==field.charAt(1)&&bytes[i+2]==field.charAt(2)){
                int titleStart=i+field.length()+1;
                int titleEnd=titleStart;
                while (bytes[titleEnd]!=0){
                    titleEnd++;
                }
                byte arr[]=new byte[titleEnd-titleStart-1];
                for (int j=0;j<arr.length;j++){
                    arr[j]=bytes[titleStart++];
                }
                return new String(arr, StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    /**
     * 解密后的音乐数据
     * @param fullPath
     * @return
     */
    private static boolean renameMusicFile(String fullPath){
        File decryptFile=new File(fullPath);
        String newName=getCorrectMusicFileName(fullPath);
        boolean succ=decryptFile.renameTo(new File(newName));
        if(succ){
            decryptFile.delete();
        }
        return succ;
    }

}