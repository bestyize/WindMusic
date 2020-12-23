package xyz.thewind.music.music.impl.qq.bean;

import com.google.gson.annotations.SerializedName;

public class LoginRespBean {
    private int code;
    private Data data=new Data();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private int code;
        private Detail data=new Detail();

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Detail getData() {
            return data;
        }

        public void setData(Detail data) {
            this.data = data;
        }

        public class Detail{
            String openid;
            @SerializedName("refresh_token")
            String refreshToken;
            @SerializedName("access_token")
            String accessToken;
            @SerializedName("expired_at")
            long expiredAt;
            @SerializedName("musicid")
            long musicId;
            @SerializedName("musickey")
            String musicKey;
            @SerializedName("musickeyCreateTime")
            long musicCreateTime;

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

            public long getMusicId() {
                return musicId;
            }

            public void setMusicId(long musicId) {
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
        }
    }
}
