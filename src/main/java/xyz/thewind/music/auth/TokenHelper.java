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
    public static Token parseToken(Cookie[] cookies){
        Token token=new Token("","");
        if(cookies==null){
            return token;
        }
        for (Cookie cookie:cookies){
            if(cookie.getName().equals("token")){
                String tokenStr=Base64Converter.decode(cookie.getValue());
                String[] tok=tokenStr.split(" ");
                if(tok.length==2){
                    return new Token(tok[0],tok[1]);
                }
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
