package xyz.thewind.music.music.impl.qq.bean;

public class RequestTemplate{
    public String module="";
    public String method="";
    public Object param=new Object();

    public RequestTemplate() {
    }

    public RequestTemplate(String module, String method, Object param) {
        this.module = module;
        this.method = method;
        this.param = param;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "{" +
                "\"module\":\"" + module + "\"" +
                ",\"method\":\"" + method + "\"" +
                ",\"param\":" + param.toString()  +
                "}";
    }
}

