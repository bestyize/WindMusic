package xyz.thewind.music.model;

public class CommonResp {
    public static CommonResp builder = new CommonResp();
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
}
