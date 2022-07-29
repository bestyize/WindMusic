package xyz.thewind.music.music.impl.qq.task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.thewind.music.music.common.MusicRedisConfig;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginResp;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.module.RequestModule;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.ApplicationUtils;
import xyz.thewind.music.util.GsonUtil;

@Component
@EnableScheduling
public class RefreshTokenTask {

    @Scheduled(initialDelay = 1000, fixedRate = 34567000)
    public void autoRefreshToken() {
        doRefreshToken();
    }

    public static String doRefreshToken() {
        RedisService redisService = ApplicationUtils.CONTEXT.getAutowireCapableBeanFactory().getBean(RedisService.class);
        if (!redisService.isRedisActive()) {
            return "redis closed";
        }
        String oldLoginInfo = redisService.get(MusicRedisConfig.REDIS_QQMUSIC_WX_LOGIN_INFO);
        QQMusicLoginToken oldToken = GsonUtil.fromJson(oldLoginInfo, QQMusicLoginToken.class);
        if (oldToken == null) {
            return "old token missed";
        }
        String newLoginInfo = RequestModule.login(oldToken);
        QQMusicLoginResp resp = GsonUtil.fromJson(newLoginInfo, QQMusicLoginResp.class);
        if (resp == null) {
            return "new token missed";
        }
        String newToken = GsonUtil.toJson(resp.data.token);
        redisService.set(MusicRedisConfig.REDIS_QQMUSIC_WX_LOGIN_INFO, newToken);
        return "update token success";
    }
}
