package xyz.thewind.music.music.impl.qq.bean.login;

import com.google.gson.annotations.SerializedName;

public class QQMusicLoginToken {

    public QQMusicLoginToken() {}
    public String openid;
    @SerializedName("refresh_token")
    public String refreshToken;
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("expired_at")
    public long expiredAt;
    @SerializedName("str_musicid")
    public String musicId;
    @SerializedName("musickey")
    public String musicKey;
    @SerializedName("musickeyCreateTime")
    public long musicCreateTime;

    public String unionid;

    private QQMusicLoginToken(Builder builder) {
        setOpenid(builder.openid);
        setRefreshToken(builder.refreshToken);
        setAccessToken(builder.accessToken);
        setExpiredAt(builder.expiredAt);
        setMusicId(builder.musicId);
        setMusicKey(builder.musicKey);
        setMusicCreateTime(builder.musicCreateTime);
        unionid = builder.unionid;
    }


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getMusicKey() {
        return musicKey;
    }

    public void setMusicKey(String musicKey) {
        this.musicKey = musicKey;
    }

    public long getMusicCreateTime() {
        return musicCreateTime;
    }

    public void setMusicCreateTime(long musicCreateTime) {
        this.musicCreateTime = musicCreateTime;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public static final class Builder {
        private String openid;
        private String refreshToken;
        private String accessToken;
        private long expiredAt;
        private String musicId;
        private String musicKey;
        private long musicCreateTime;
        private String unionid;

        public Builder() {
        }

        public Builder openid(String val) {
            openid = val;
            return this;
        }

        public Builder refreshToken(String val) {
            refreshToken = val;
            return this;
        }

        public Builder accessToken(String val) {
            accessToken = val;
            return this;
        }

        public Builder expiredAt(long val) {
            expiredAt = val;
            return this;
        }

        public Builder musicId(String val) {
            musicId = val;
            return this;
        }

        public Builder musicKey(String val) {
            musicKey = val;
            return this;
        }

        public Builder musicCreateTime(long val) {
            musicCreateTime = val;
            return this;
        }

        public Builder unionid(String val) {
            unionid = val;
            return this;
        }

        public QQMusicLoginToken build() {
            return new QQMusicLoginToken(this);
        }
    }
}
