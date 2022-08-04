package xyz.thewind.music.netool.censys.bean.search;

import java.util.ArrayList;
import java.util.List;

public class HostInfo {
    public String ip;
    public List<HostService> services = new ArrayList<>();


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<HostService> getServices() {
        return services;
    }

    public void setServices(List<HostService> services) {
        this.services = services;
    }
}
