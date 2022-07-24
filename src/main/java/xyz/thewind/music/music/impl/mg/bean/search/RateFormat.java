package xyz.thewind.music.music.impl.mg.bean.search;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.music.common.MusicCommonConfig;

public class RateFormat {

    public String resourceType;
    @SerializedName("formatType")
    public String quality;
    public String size;
    @SerializedName(value = "fileType", alternate = {"androidFileType"})
    public String fileType;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getQuality() {
        if ("z3d".equals(quality)) {
            return MusicCommonConfig.QUALITY_EQ;
        }
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
