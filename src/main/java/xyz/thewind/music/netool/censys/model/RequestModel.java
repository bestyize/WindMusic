package xyz.thewind.music.netool.censys.model;

import xyz.thewind.music.netool.censys.bean.LicenseServer;
import xyz.thewind.music.netool.censys.bean.search.HostInfo;
import xyz.thewind.music.netool.censys.bean.search.HostService;
import xyz.thewind.music.netool.censys.bean.search.SearchHostResponse;
import xyz.thewind.music.netool.censys.bean.search.SearchHostResult;
import xyz.thewind.music.netool.censys.config.CensysConfig;
import xyz.thewind.music.network.util.HttpUtil;
import xyz.thewind.music.util.GsonUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestModel {

    public static void main(String[] args) {
        System.out.println(GsonUtil.toJson(searchJBLicenseServer()));
    }

    public static List<HostInfo>  searchHost(String keyword, int num) {
        String url = CensysConfig.SEARCH_HOST_API.replace("_keyword_", URLEncoder.encode(keyword, StandardCharsets.UTF_8)).replace("_num_", num + "");
        String response = HttpUtil.get(url, CensysConfig.headerMap);
        return parseSearchHostResult(response);

    }

    public static List<String> searchJBLicenseServer() {
        List<String> licenseServers = new ArrayList<>();
        List<HostInfo> hostInfos = searchJBHost();
        for (HostInfo info : hostInfos) {
            String licenseServer = getValidLicenseServerUrl(info);
            if (licenseServer != null) {
                licenseServers.add(licenseServer);
            }
        }
        return licenseServers;
    }

    private static List<HostInfo> searchJBHost() {
        return searchHost(CensysConfig.JB_HOST, 100);
    }

    private static String getValidLicenseServerUrl(HostInfo hostInfo) {
        if (hostInfo == null) {
            return null;
        }
        List<HostService> hostServices = hostInfo.services;
        for (HostService service : hostServices) {
            if (!service.isHttp()) {
                continue;
            }
            LicenseServer server = new LicenseServer(service.getScheme(), hostInfo.getIp(), service.port);
            if (isValidLicenseServer(server)) {
                return server.getLicenseServer();
            }
        }
        return null;

    }

    private static boolean isValidLicenseServer(LicenseServer server) {
        System.out.println("valid start");
        String response = HttpUtil.get(server.getLicenseServer() + CensysConfig.JB_VERIFY);
        System.out.println("valid finish");
        return response.contains("<responseCode>OK</responseCode>");

    }

    private static List<HostInfo> parseSearchHostResult(String response) {
        List<HostInfo> hostInfoList = new ArrayList<>();
        SearchHostResponse hostResponse = GsonUtil.fromJson(response, SearchHostResponse.class);
        if (hostResponse == null) {
            return hostInfoList;
        }
        return hostResponse.result.hostInfos;
    }

}
