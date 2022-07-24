package xyz.thewind.music.service;

public interface RedisService {
    void set(String key, String value);

    String get(String key);

    void setExpire(String key, long expire);

    void remove(String key);

    Long increment(String key, long gap);

    boolean isRedisActive();
}
