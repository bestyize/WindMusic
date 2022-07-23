package xyz.thewind.music.music.impl.qq.bean.login;

import com.google.gson.annotations.SerializedName;

public class QQMusicLoginResp {
    public int code;
    public Data data = new Data();

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

    public static class Data {

        public int code;

        @SerializedName("data")
        public QQMusicLoginToken token = new QQMusicLoginToken();

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public QQMusicLoginToken getToken() {
            return token;
        }

        public void setToken(QQMusicLoginToken token) {
            this.token = token;
        }


    }
}
