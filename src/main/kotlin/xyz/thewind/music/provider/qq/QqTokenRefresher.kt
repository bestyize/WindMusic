package xyz.thewind.music.provider.qq

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import xyz.thewind.music.common.GsonUtil
import xyz.thewind.music.provider.qq.dto.QqLoginResponse
import xyz.thewind.music.provider.qq.dto.QqLoginToken
import xyz.thewind.music.store.MusicSettings

/**
 * 定时用存储中的旧登录 token 换取新 token 并写回，避免 musickey 过期后无法获取 VIP 音质。
 * 也可以通过 `/api/updatetoken` 手动触发 [refresh]。
 */
@Component
class QqTokenRefresher(private val settings: MusicSettings) {
    private val log = LoggerFactory.getLogger(QqTokenRefresher::class.java)

    // 约 9.6 小时刷新一次
    @Scheduled(initialDelay = 1000, fixedRate = 34567000)
    fun scheduledRefresh() {
        log.info("refresh qq music token: {}", refresh())
    }

    /** @return 刷新结果，取值与旧版一致，会原样返回给 `/api/updatetoken` 的调用方 */
    fun refresh(): String {
        if (!settings.isWritable) return "redis closed"
        val oldToken = GsonUtil.fromJson<QqLoginToken>(settings.qqLoginInfo) ?: return "old token missed"
        val resp = GsonUtil.fromJson<QqLoginResponse>(QqApi.login(oldToken)) ?: return "new token missed"
        // 旧版在 data 为 null 时抛 NPE，这里按获取失败处理
        val data = resp.data ?: return "new token missed"
        // 注意：与旧版一致，响应里没有 token 时也会把空 token 写回
        settings.qqLoginInfo = GsonUtil.toJson(data.token)
        return "update token success"
    }
}
