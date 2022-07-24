package xyz.thewind.music.music.impl;

import org.junit.Test;

import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginResp;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.module.RequestModule;
import xyz.thewind.music.util.GsonConverter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QQMusicTest {
    int index = 0;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        new QQMusicTest().doRefresh();
    }

    @Test
    public void testEnc() {
        System.out.println(RequestModule.getHotSingerList());
    }

    @Test
    public void testLogin() {
        String musicKey = "W_X_5NVTOP7m__XAq_Jzay3CAp188MQVhEJkTjVY-mGx37BYZ2qDh4FNFwzQFI1oIAqYAe4iKMg1LMzo";
        String musicId = "1152921504741490647";
        String accessToken = "";
        String refreshToken = "59_qz4-IC7oLI7GSxkICpvF8wM3ZAVjMZXNCPMbJyc4RJDEvaTmPRVjYeo3DRsNY7jNmfPWexkKoO2KcHqupW0m5QbVMGehRvzI7YEvac6TDP0";
        String openid = "opCFJw8b_qxl5vdjyZTI6u9Xiz7k";
        String unionid = "oqFLxslbFUZPalHgFv99RS-n6DPQ";
        QQMusicLoginToken token = new QQMusicLoginToken.Builder()
                .musicKey(musicKey)
                .musicId(musicId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .openid(openid)
                .unionid(unionid)
                .build();
        String resp = RequestModule.login(token);
        QQMusicLoginResp loginResp = GsonConverter.fromJson(resp, QQMusicLoginResp.class);
        System.out.println(GsonConverter.toJson(loginResp.data.token));
    }

    @Test
    public void testRefreshLogin() {
//        RefreshTokenTaskManager.getInstance().startRefresh(false);
        doRefresh();

    }

    private void doRefresh() {
        scheduledExecutorService.schedule(mRunnable, 1, TimeUnit.SECONDS);
    }    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            System.out.println(index++);
            doRefresh();
        }
    };


}
