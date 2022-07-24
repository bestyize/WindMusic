package xyz.thewind.music.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.thewind.music.service.RedisService;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {

        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void setExpire(String key, long expire) {
        stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Long increment(String key, long gap) {
        return stringRedisTemplate.opsForValue().increment(key, gap);
    }

    @Override
    public boolean isRedisActive() {
        boolean connectState;
        try {
            connectState = !Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection().isClosed();
        } catch (Exception e) {
            connectState = false;
        }
        return connectState;
    }


}
