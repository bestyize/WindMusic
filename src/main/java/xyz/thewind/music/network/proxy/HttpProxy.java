package xyz.thewind.music.network.proxy;

public class HttpProxy {
    public String url;

    public HttpProxy() {
    }

    public HttpProxy(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

