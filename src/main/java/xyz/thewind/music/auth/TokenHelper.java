package xyz.thewind.music.auth;

import xyz.thewind.music.model.Token;
import xyz.thewind.music.model.User;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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

    public static void setCookie(HttpServletResponse response, User user, String userId, String password){
        Cookie inf=new Cookie("info", Base64Converter.encode(GsonConverter.toJson(user)));
        inf.setPath("/");
        inf.setMaxAge(86400);
        Cookie tok=new Cookie("token",TokenHelper.generateToken(userId,password));
        tok.setPath("/");
        inf.setMaxAge(86400);
        response.addCookie(inf);
        response.addCookie(tok);
    }
}
