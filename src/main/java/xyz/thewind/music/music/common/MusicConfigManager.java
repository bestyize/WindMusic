package xyz.thewind.music.music.common;

import org.springframework.beans.factory.annotation.Autowired;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginResp;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.task.RefreshTokenTaskManager;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.GsonConverter;

import java.lang.ref.WeakReference;

public class MusicConfigManager {

    private WeakReference<RedisService> redisService;

    private static class Inner {
        public static MusicConfigManager INSTANCE = new MusicConfigManager();
    }

    private MusicConfigManager() {}

    public static MusicConfigManager getInstance() {
        return Inner.INSTANCE;
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = new WeakReference<>(redisService);
    }

    public void refreshMusicToken(boolean force) {
        RefreshTokenTaskManager.getInstance().startRefresh(force);
    }

    public QQMusicLoginToken getQQMusicLoginToken() {
        RedisService service = redisService.get();
        if (service == null || !service.isRedisActive()) {
            return null;
        }
        QQMusicLoginToken token = GsonConverter.fromJson(
                service.get(MusicConfig.REDIS_QQMUSIC_WX_LOGIN_INFO),
                QQMusicLoginToken.class);
        if (token == null) {
            return null;
        }
        return token;
    }


}
