package xyz.thewind.music.provider.qq.dto

import com.google.gson.annotations.SerializedName

/**
 * QQ 音乐（微信登录）的登录凭证，以 JSON 形式保存在 `MusicSettings.qqLoginInfo`。
 *
 * 该类同时会被 Gson 序列化后写回存储，字段集合、名称与顺序必须与旧版保持一致。
 */
class QqLoginToken {
    var openid: String? = null

    @SerializedName("refresh_token")
    var refreshToken: String? = null

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("expired_at")
    var expiredAt: Long = 0

    @SerializedName("str_musicid")
    var musicId: String? = null

    @SerializedName("musickey")
    var musicKey: String? = null

    @SerializedName("musickeyCreateTime")
    var musicKeyCreateTime: Long = 0

    var unionid: String? = null
}

/** `music.login.LoginServer.Login` 的响应：`{"code":0,"data":{"code":0,"data":{...token...}}}` */
class QqLoginResponse {
    // 旧版带默认值：响应里缺少 data 时会把一个空 token 写回存储，这里保持一致
    var data: Data? = Data()

    class Data {
        @SerializedName("data")
        var token: QqLoginToken? = QqLoginToken()
    }
}
