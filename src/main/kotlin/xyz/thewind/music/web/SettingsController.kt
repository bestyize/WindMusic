package xyz.thewind.music.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.thewind.music.common.Codec
import xyz.thewind.music.common.javaSplit
import xyz.thewind.music.model.CommonResp
import xyz.thewind.music.provider.qq.QqTokenRefresher
import xyz.thewind.music.store.MusicSettings

/**
 * 运行时配置接口：写入各平台 VIP Cookie / 强制音源，查看当前配置，手动刷新 QQ 音乐登录态。
 *
 * 配置了 `wind.admin-token` 时，这些接口必须带相同的 `token` 参数；未配置时与旧版一样对外开放。
 */
@RestController
@RequestMapping("/api")
class SettingsController(
    private val settings: MusicSettings,
    private val qqTokenRefresher: QqTokenRefresher,
    @Value("\${wind.admin-token:}") private val adminToken: String,
) {

    /**
     * @param params Base64 编码的 `key1=value1;key2=value2`，可用的 key 见 [MusicSettings.Keys]
     */
    @RequestMapping("/configupdate")
    fun update(params: String?, token: String?): CommonResp {
        if (!authorized(token)) return UNAUTHORIZED
        if (params.isNullOrEmpty() || !settings.isWritable) return CommonResp(msg = "更新失败")
        Codec.base64Decode(params).javaSplit(";")
            .map { it.javaSplit("=") }
            .filter { it.size == 2 }
            .forEach { (key, value) -> settings.set(key, value) }
        return CommonResp(code = 0, msg = "更新成功")
    }

    @RequestMapping("/getconfig")
    fun get(token: String?): CommonResp =
        if (!authorized(token)) UNAUTHORIZED else CommonResp(code = 0, msg = "拉取成功", obj = settings.snapshot())

    @RequestMapping("/updatetoken")
    fun refreshQqToken(token: String?): CommonResp =
        if (!authorized(token)) UNAUTHORIZED else CommonResp(code = 0, msg = qqTokenRefresher.refresh())

    private fun authorized(token: String?): Boolean = adminToken.isEmpty() || token == adminToken

    private companion object {
        val UNAUTHORIZED = CommonResp(code = 401, msg = "token 无效")
    }
}
