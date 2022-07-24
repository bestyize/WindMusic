package xyz.thewind.music.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.logging.Logger;

public class GsonConverter {
    private static final Gson gson=new GsonBuilder().disableHtmlEscaping().create();
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    public static void printToJson(Object obj){
        System.out.println(gson.toJson(obj));
    }
    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        if (classOfT == null) {
            return null;
        }
        T t = null;
        try {
            t = gson.fromJson(json,classOfT);
        } catch (Exception e) {
            System.out.println(e);
        }

        return t;
    }
}
