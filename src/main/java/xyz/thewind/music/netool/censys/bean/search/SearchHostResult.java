package xyz.thewind.music.netool.censys.bean.search;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchHostResult {
    public String query;
    public int total;
    @SerializedName("hits")
    public List<HostInfo> hostInfos = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<HostInfo> getHostInfos() {
        return hostInfos;
    }

    public void setHostInfos(List<HostInfo> hostInfos) {
        this.hostInfos = hostInfos;
    }
}
