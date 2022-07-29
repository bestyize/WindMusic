package xyz.thewind.music.tool.down;

import java.io.File;
import java.util.Map;

public class DownloadParams {
    public String downloadLink;

    public String saveName;

    public Map<String, String> headerMap;

    private DownloadParams(Builder builder) {
        downloadLink = builder.downloadLink;
        saveName = builder.saveName;
        headerMap = builder.headerMap;
    }


    public static final class Builder {
        private String downloadLink;
        private String saveName;
        private Map<String, String> headerMap;

        public Builder() {
        }

        public Builder downloadLink(String val) {
            downloadLink = val;
            return this;
        }

        public Builder saveName(String val) {
            saveName = val;
            return this;
        }

        public Builder headerMap(Map<String, String> val) {
            headerMap = val;
            return this;
        }

        public DownloadParams build() {
            return new DownloadParams(this);
        }
    }
}
