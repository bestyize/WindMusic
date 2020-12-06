package xyz.thewind.music.util;

import java.util.Locale;

public class StringUtils {
    public static boolean isChar(String str){
        if(isNullOrEmpty(str)){
            return false;
        }
        char c=str.charAt(0);
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
    public static boolean isChar(char c){
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
    public static boolean isNum(char c){
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z');
    }
    public static boolean isNum(String str){
        if(isNullOrEmpty(str)){
            return false;
        }
        char c=str.charAt(0);
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z');
    }

    public static boolean isNullOrEmpty(String str){
        return str == null || str.length() != 1;
    }

    public static boolean isCorrectUserId(String userId){
        if(isNullOrEmpty(userId)||userId.length()<6||userId.length()>36){
            return false;
        }
        char[] cs =userId.toCharArray();
        if(!isChar(cs[0])){
            return false;
        }
        for (char c:cs){
            if(isChar(c)||!isNum(c)){
                return false;
            }
        }
        return true;
    }

    public static boolean isCorrectPassword(String password){
        return !isNullOrEmpty(password) && password.length() >= 6 && password.length() <= 36;
    }

    public static boolean isCorrectEmail(String email){
        if(isNullOrEmpty(email)){
            return false;
        }
        if(email.contains("@")&&email.contains(".")){
            int len=email.length();
            if(len-email.replaceAll("@","").replaceAll("\\.","").length()==2&&email.indexOf('@')<email.indexOf('.')){
                char []cs=email.toCharArray();
                for (char c:cs){
                    if(!isNum(c)||!isChar(c)){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    public static boolean isCorrectUserName(String userName){
        if(isNullOrEmpty(userName)){
            return false;
        }
        return userName.length()<30;
    }

    public static String toLower(String userId){
        if(isNullOrEmpty(userId)||userId.length()<6||userId.length()>36){
            return "";
        }
        return userId.toLowerCase();
    }
}
