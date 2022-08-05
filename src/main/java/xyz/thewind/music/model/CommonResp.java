package xyz.thewind.music.model;

public class CommonResp {

    public int code;
    public String msg;
    public Object obj;

    public CommonResp() {
        this.code = -1;
    }

    public CommonResp(int code, String msg, Object obj) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }

    private CommonResp(Builder builder) {
        code = builder.code;
        msg = builder.msg;
        obj = builder.obj;
    }

    public CommonResp code(int code) {
        this.code = code;
        return this;
    }

    public CommonResp msg(String msg) {
        this.msg = msg;
        return this;
    }

    public CommonResp obj(Object obj) {
        this.obj = obj;
        return this;
    }


    public static final class Builder {
        private int code;
        private String msg;
        private Object obj;

        public Builder() {
        }

        public Builder code(int val) {
            code = val;
            return this;
        }

        public Builder msg(String val) {
            msg = val;
            return this;
        }

        public Builder obj(Object val) {
            obj = val;
            return this;
        }

        public CommonResp build() {
            return new CommonResp(this);
        }
    }
}
