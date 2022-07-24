package xyz.thewind.music.music.common;

import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.task.RefreshTokenTask;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.ApplicationUtils;
import xyz.thewind.music.util.GsonConverter;

public class MusicConfigManager {

    private MusicConfigManager() {
    }

    public static MusicConfigManager getInstance() {
        return Inner.INSTANCE;
    }

    public QQMusicLoginToken getQQMusicLoginToken() {
        RedisService service = ApplicationUtils.CONTEXT.getAutowireCapableBeanFactory().getBean(RedisService.class);
        if (!service.isRedisActive()) {
            return null;
        }
        return GsonConverter.fromJson(
                service.get(MusicRedisConfig.REDIS_QQMUSIC_WX_LOGIN_INFO),
                QQMusicLoginToken.class);
    }

    public String refreshToken() {
        return RefreshTokenTask.doRefreshToken();
    }

    private static class Inner {
        public static MusicConfigManager INSTANCE = new MusicConfigManager();
    }


}
