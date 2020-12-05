package xyz.thewind.music.auth;

import xyz.thewind.music.model.Token;
import xyz.thewind.music.util.Base64Converter;

public class TokenHelper {
    public static String generateToken(String userId,String password){
        return Base64Converter.encode(userId+" "+password);
    }
    //userId=1234;password=123456
    public static Token parseToken(String cookie){
        cookie=Base64Converter.decode(cookie);
        String userId="";
        String password="";
        Token token=new Token(userId,password);
        if(cookie==null){
            return token;
        }
        cookie=cookie.replaceAll(" ","");
        String[] cookies=cookie.split(";");
        if(cookies.length==0){
            return token;
        }
        for (String cookieSplice:cookies){
            String[] pair=cookieSplice.split("=");
            if(cookies.length!=2){
                return token;
            }
            if(pair[0].equals("userId")){
                token.setUserId(pair[1]);
            }
            if(pair[0].equals("password")){
                token.setPassword(pair[1]);
            }
        }
        return token;
    }
}
