package xyz.thewind.music.network.proxy;

import xyz.thewind.music.music.impl.kg.KgMusicModel;
import xyz.thewind.music.network.util.HttpUtil;
import xyz.thewind.music.util.GsonUtil;
import xyz.thewind.music.util.WLog;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpProxyManager {

    private static final String TAG = "HttpProxyManager";

    private List<Proxy> mProxyStack = new CopyOnWriteArrayList<>();

    private HttpProxyManager() {}

    private static class Inner {
        public static final HttpProxyManager INSTANCE = new HttpProxyManager();
    }

    public static HttpProxyManager getInstance() {
        return Inner.INSTANCE;
    }

    public Proxy getProxy(Proxy.Type type, String ip, int port) {
        return new Proxy(type, new InetSocketAddress(ip, port));
    }

//    public List<Proxy> getProxyList() {
//        return readProxyFromFile();
//    }

    private List<Proxy> readProxyFromFile(Proxy.Type type, String path) {
        List<Proxy> proxies = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        } catch (FileNotFoundException e) {
            WLog.i(TAG, "readProxyFromFile, fileNotFound");
        }
        String line = "";
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                WLog.e(TAG, "readFromFile, error, e = " + e);
            }
            String[] pair = line.split(":");
            if (pair.length == 2) {
                proxies.add(getProxy(type, pair[0], Integer.parseInt(pair[1])));
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return proxies;
    }

    public static void main(String[] args) {
//        // [HTTP @ /8.219.97.248:80, HTTP @ /189.232.81.227:8080, HTTP @ /46.53.191.12:3128, HTTP @ /133.18.194.111:8080]
//        WLog.i(TAG, KgMusicModel.searchMusicWithProxy("十年", 100, getInstance().getProxy(Proxy.Type.SOCKS, "45.126.125.235", 44251)));
        getInstance().verifyProxyList(getInstance().readProxyFromFile(Proxy.Type.HTTP, "/home/read/Desktop/httpproxy.txt"));
    }

    public List<Proxy> requestHttpProxyFromWeb(Proxy.Type type, String url) {
        if (url == null) {
            url = "https://raw.githubusercontent.com/roosterkid/openproxylist/main/HTTPS_RAW.txt";
        }
        List<Proxy> proxies = new ArrayList<>();
        String resp = HttpUtil.get(url);
        String[] ips = resp.split("\n");
        for (String ip : ips) {
            String[] pair = ip.split(":");
            if (pair.length == 2) {
                proxies.add(getProxy(type, pair[0], Integer.parseInt(pair[1])));
            }
        }
        return verifyProxyList(proxies);
    }

    public List<Proxy> verifyProxyList(List<Proxy> proxies) {
        if (proxies == null || proxies.size() == 0) {
            return new ArrayList<>();
        }
        final ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch latch = new CountDownLatch(proxies.size());
        final List<Proxy> validProxyList = new Vector<>();
        for (final Proxy proxy : proxies) {
            service.submit(() -> {
                boolean ok = HttpUtil.isValidProxy(proxy);
                WLog.i(TAG, "verifyProxyList, ok, = " + ok + ", proxy = " + proxy);
                if (ok) {
                    validProxyList.add(proxy);
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            WLog.e(TAG, "verifyProxyList, error = " + e);
        }
        WLog.i(TAG, validProxyList);
        return validProxyList;
    }


}
