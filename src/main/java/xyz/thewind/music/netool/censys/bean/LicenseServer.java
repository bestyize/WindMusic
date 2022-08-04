package xyz.thewind.music.netool.censys.bean;

import xyz.thewind.music.netool.censys.config.CensysConfig;

public class LicenseServer {
    private String scheme;
    private String ip;
    private int port;

    public LicenseServer(String scheme, String ip, int port) {
        this.scheme = scheme;
        this.ip = ip;
        this.port = port;
    }

    public String getLicenseServer() {
        return scheme + ip + ":" + port;
    }

}
