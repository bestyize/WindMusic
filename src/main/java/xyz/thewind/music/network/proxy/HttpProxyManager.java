package xyz.thewind.music.network.proxy;

import xyz.thewind.music.network.util.HttpUtil;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class HttpProxyManager {

    private HttpProxyManager() {}

    private static class Inner {
        public static final HttpProxyManager INSTANCE = new HttpProxyManager();
    }

    public static HttpProxyManager getInstance() {
        return Inner.INSTANCE;
    }

    public Proxy getProxy(String ip, int port) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    }

    public List<Proxy> getProxyList() {
        return new ArrayList<>() {{
            add(getProxy("127.0.0.1", 1080));
        }};
    }

    public static void main(String[] args) {
        for (Proxy proxy : getInstance().getProxyList()) {
            String resp = HttpUtil.get("https://baidu.com/", null, proxy);
            System.out.println(resp);
        }

    }
}
