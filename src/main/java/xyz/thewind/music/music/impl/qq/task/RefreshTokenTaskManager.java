package xyz.thewind.music.music.impl.qq.task;

import org.springframework.beans.factory.annotation.Autowired;
import xyz.thewind.music.music.common.MusicConfig;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginResp;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.module.RequestModule;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.GsonConverter;

import java.util.concurrent.*;

public class RefreshTokenTaskManager {

    @Autowired
    private RedisService redisService;

    private boolean isActive = false;

    private static final long MIN_SCHEDULE_TIME_SECOND = 60 * 60 * 5;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private RefreshTokenTaskManager() {

    }
    private static class Inner {
        public static final RefreshTokenTaskManager INSTANCE = new RefreshTokenTaskManager();
    }

    public static RefreshTokenTaskManager getInstance() {
        return Inner.INSTANCE;
    }

    public void startRefresh(boolean force) {
        if (force) {
            handleRefresh();
        }
        if (isActive) {
            return;
        }
        isActive = true;
        doRefresh();
    }

    private void doRefresh() {
        scheduledExecutorService.schedule(() -> {
            handleRefresh();
            doRefresh();
        }, MIN_SCHEDULE_TIME_SECOND + (long)(Math.random() * 60 * 60 * 2), TimeUnit.SECONDS);
    }

    private void handleRefresh() {
        if (!redisService.isRedisActive()) {
            return;
        }
        String oldLoginInfo = redisService.get(MusicConfig.REDIS_QQMUSIC_WX_LOGIN_INFO);
        QQMusicLoginToken oldToken = GsonConverter.fromJson(oldLoginInfo, QQMusicLoginToken.class);
        if (oldToken == null) {
            return;
        }
        String newLoginInfo = RequestModule.login(oldToken);
        QQMusicLoginResp resp = GsonConverter.fromJson(newLoginInfo, QQMusicLoginResp.class);
        if (resp == null) {
            return;
        }
        String newToken = GsonConverter.toJson(resp.data.token);
        redisService.set(MusicConfig.REDIS_QQMUSIC_WX_LOGIN_INFO, newToken);
    }
}
