package xyz.thewind.music.music.impl.qq.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 构造请求结构，所有请求都可以是这样的
 *
 * {
 *     "comm": {
 *         "ct": 24,
 *         "cv": 0
 *     },
 *     "singerList": {
 *         "module": "Music.SingerListServer",
 *         "method": "get_singer_list",
 *         "param": {
 *             "area": -100,
 *             "sex": -100,
 *             "genre": -100,
 *             "index": -100,
 *             "sin": 0,
 *             "cur_page": 1
 *         }
 *     }
 * }
 */
public class RequestTemplateBuilder {
    private Map<String,Object> requestTemplateMap=new HashMap<>();
    public RequestTemplateBuilder add(String name, Object req){
        requestTemplateMap.put(name,req);
        return this;
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        for (String key: requestTemplateMap.keySet()){
            sb.append("\"").append(key).append("\":").append(requestTemplateMap.get(key).toString()).append(",");
        }
        sb.substring(0,sb.length()-1);
        return "{"+sb.substring(0,sb.length()-1)+"}";
    }
}
