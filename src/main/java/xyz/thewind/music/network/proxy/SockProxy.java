package xyz.thewind.music.network.proxy;

public class SockProxy {

    public String url;

    public SockProxy(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
