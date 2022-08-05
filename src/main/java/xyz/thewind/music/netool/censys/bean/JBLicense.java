package xyz.thewind.music.netool.censys.bean;

public class JBLicense {

    public String serverUrl;
    public boolean valid;

    public JBLicense() {
    }

    public JBLicense(String serverUrl, boolean valid) {
        this.serverUrl = serverUrl;
        this.valid = valid;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
