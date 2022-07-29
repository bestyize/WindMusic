package xyz.thewind.music.music.common;

import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.task.RefreshTokenTask;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.ApplicationUtils;
import xyz.thewind.music.util.GsonConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicConfigManager {
    private static final List<String> configKeyList = new ArrayList<>() {{
        add(MusicRedisConfig.REDIS_KGMUSIC_UID);
        add(MusicRedisConfig.REDIS_KGMUSIC_TOKEN);
        add(MusicRedisConfig.REDIS_WYMUSIC_TOKEN_KEY);
        add(MusicRedisConfig.REDIS_MUSIC_SRC);
        add(MusicRedisConfig.REDIS_QQMUSIC_WX_LOGIN_INFO);
    }}
;

    private MusicConfigManager() {
    }

    public static MusicConfigManager getInstance() {
        return Inner.INSTANCE;
    }

    public String getSongSrc() {
        return getConfigStr(MusicRedisConfig.REDIS_MUSIC_SRC);
    }

    public QQMusicLoginToken getQQMusicLoginToken() {
        RedisService service = getRedisService();
        if (service == null || !service.isRedisActive()) {
            return null;
        }
        return GsonConverter.fromJson(
                service.get(MusicRedisConfig.REDIS_QQMUSIC_WX_LOGIN_INFO),
                QQMusicLoginToken.class);
    }

    public String getWyMusicToken() {
        return getConfigStr(MusicRedisConfig.REDIS_WYMUSIC_TOKEN_KEY);
    }

    public String getKgUid() {
        return getConfigStr(MusicRedisConfig.REDIS_KGMUSIC_UID);
    }

    public String getKgToken() {
        return getConfigStr(MusicRedisConfig.REDIS_KGMUSIC_TOKEN);
    }

    public String refreshToken() {
        return RefreshTokenTask.doRefreshToken();
    }

    public String getConfigStr(String key) {
        RedisService service = getRedisService();
        if (service == null || !service.isRedisActive()) {
            return null;
        }
        return service.get(key);
    }

    public Map<String, String> loadConfig() {
        if (configKeyList.size() == 0) {
            configKeyList.add(MusicRedisConfig.REDIS_KGMUSIC_UID);
            configKeyList.add(MusicRedisConfig.REDIS_KGMUSIC_TOKEN);
            configKeyList.add(MusicRedisConfig.REDIS_WYMUSIC_TOKEN_KEY);
            configKeyList.add(MusicRedisConfig.REDIS_MUSIC_SRC);
            configKeyList.add(MusicRedisConfig.REDIS_QQMUSIC_WX_LOGIN_INFO);
        }
        Map<String, String> configMap = new HashMap<>();
        for (String key : configKeyList) {
            String val = MusicConfigManager.getInstance().getConfigStr(key);
            configMap.put(key, val);
        }
        return configMap;
    }


    public RedisService getRedisService() {
        RedisService service = ApplicationUtils.CONTEXT.getAutowireCapableBeanFactory().getBean(RedisService.class);
        if (!service.isRedisActive()) {
            return null;
        }
        return service;
    }

    private static class Inner {
        public static MusicConfigManager INSTANCE = new MusicConfigManager();
    }


}
