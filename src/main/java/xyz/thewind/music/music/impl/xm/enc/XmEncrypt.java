package xyz.thewind.music.music.impl.xm.enc;

import com.sun.jndi.toolkit.url.Uri;
import xyz.thewind.music.util.Md5Encrypt;

import java.net.MalformedURLException;

public class XmEncrypt {
    public static String requestSign(String requestLink,String xmSigToken){
        Uri uri= null;
        try {
            uri = new Uri(requestLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String path=uri.getPath();
        String requestParams= Md5Encrypt.convertToMd5(xmSigToken+"_xmMain_"+path+"_");
        return requestParams;
    }
}
