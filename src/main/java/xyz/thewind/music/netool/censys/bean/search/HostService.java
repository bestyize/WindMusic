package xyz.thewind.music.netool.censys.bean.search;

import com.google.gson.annotations.SerializedName;

public class HostService {
    public int port;
    @SerializedName("service_name")
    public String serviceName;
    public String certificate;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public boolean isHttp() {
        return "HTTP".equals(serviceName);
    }

    public String getScheme() {
        if (!"HTTP".equals(serviceName)) {
            return null;
        }
        if (certificate == null || certificate.length() == 0) {
            return "http://";
        }
        return "https://";
    }
}
