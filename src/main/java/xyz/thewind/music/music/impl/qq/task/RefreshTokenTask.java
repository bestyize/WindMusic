package xyz.thewind.music.music.impl.qq.task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.thewind.music.music.common.MusicConfig;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginResp;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.module.RequestModule;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.ApplicationUtils;
import xyz.thewind.music.util.GsonConverter;

@Component
@EnableScheduling
public class RefreshTokenTask {

    @Scheduled(initialDelay = 1000, fixedRate = 34567)
    public void autoRefreshToken() {
        doRefreshToken();
    }

    public static String doRefreshToken() {
        RedisService redisService = ApplicationUtils.CONTEXT.getAutowireCapableBeanFactory().getBean(RedisService.class);
        if (!redisService.isRedisActive()) {
            return "redis closed";
        }
        String oldLoginInfo = redisService.get(MusicConfig.REDIS_QQMUSIC_WX_LOGIN_INFO);
        QQMusicLoginToken oldToken = GsonConverter.fromJson(oldLoginInfo, QQMusicLoginToken.class);
        if (oldToken == null) {
            return "old token missed";
        }
        String newLoginInfo = RequestModule.login(oldToken);
        QQMusicLoginResp resp = GsonConverter.fromJson(newLoginInfo, QQMusicLoginResp.class);
        if (resp == null) {
            return "new token missed";
        }
        String newToken = GsonConverter.toJson(resp.data.token);
        redisService.set(MusicConfig.REDIS_QQMUSIC_WX_LOGIN_INFO, newToken);
        return "update token success";
    }
}
