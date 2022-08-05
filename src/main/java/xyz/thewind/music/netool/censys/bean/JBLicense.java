package xyz.thewind.music.netool.censys.bean;

public class JBLicense {

    public String serverUrl;
    public boolean isValid;

    public JBLicense() {
    }

    public JBLicense(String serverUrl, boolean isValid) {
        this.serverUrl = serverUrl;
        this.isValid = isValid;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
