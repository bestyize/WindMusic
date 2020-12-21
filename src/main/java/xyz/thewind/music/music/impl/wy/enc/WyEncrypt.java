package xyz.thewind.music.music.impl.wy.enc;

import com.sun.jndi.toolkit.url.Uri;

import xyz.thewind.music.util.Md5Encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class WyEncrypt{
    public static final byte BYTE_MAX_VALUE = -1;
    public static String encKey="e82ckenh8dichen8";

    public static String eapiEncrypt(String url,String clearParams){
        try {
            Uri uri = new Uri(url);
            String path=uri.getPath().replaceFirst("/eapi/","/api/");
            String message="nobody"+path+"use"+clearParams+"md5forencrypt";
            String data=path+"-36cd479b6b5-"+clearParams+"-36cd479b6b5-"+ Md5Encrypt.convertToMd5(message);
            return WyEncrypt.encodeCipherText(WyEncrypt.encrypt(data,encKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String eapiDecrypt(String cipherText){
        return new String(WyEncrypt.decrypt(WyEncrypt.decodeCipherText(cipherText),WyEncrypt.encKey));
    }

    private static byte[] encrypt(String text,String key){
        return encrypt(text,key,null,"AES/ECB/PKCS5Padding");
    }

    private static byte[] decrypt(byte[]cipherText,String key){
        return decrypt(cipherText,key,null,"AES/ECB/PKCS5Padding");
    }

    private static byte[] encrypt(String text,String key,String iv,String encMode){
        try{
            SecretKeySpec spec=new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"AES");
            Cipher cipher=Cipher.getInstance(encMode);
            if(iv==null){
                cipher.init(Cipher.ENCRYPT_MODE,spec);
            }else {
                cipher.init(Cipher.ENCRYPT_MODE,spec,new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            }
            return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] decrypt(byte[] cipherText,String key,String iv,String encMode){
        try{
            SecretKeySpec spec=new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"AES");
            Cipher cipher=Cipher.getInstance(encMode);
            if(iv==null){
                cipher.init(Cipher.DECRYPT_MODE,spec);
            }else {
                cipher.init(Cipher.DECRYPT_MODE,spec,new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            }
            return cipher.doFinal(cipherText);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String encodeCipherText(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString((b2 & BYTE_MAX_VALUE));
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }

            hexString=hexString.substring(hexString.length()-2);

            stringBuffer.append(hexString.toUpperCase());
        }
        return stringBuffer.toString();
    }

    private static byte[] decodeCipherText(String str) {
        if (str.length() < 1) {
            return null;
        }
        byte[] bArr = new byte[(str.length() / 2)];
        for (int i2 = 0; i2 < str.length() / 2; i2++) {
            int i3 = i2 * 2;
            int i4 = i3 + 1;
            bArr[i2] = (byte) ((Integer.parseInt(str.substring(i3, i4), 16) * 16) + Integer.parseInt(str.substring(i4, i3 + 2), 16));
        }
        return bArr;
    }

}