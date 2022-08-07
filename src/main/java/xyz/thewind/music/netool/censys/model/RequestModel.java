package xyz.thewind.music.netool.censys.model;

import xyz.thewind.music.netool.censys.bean.JBLicense;
import xyz.thewind.music.netool.censys.bean.LicenseServer;
import xyz.thewind.music.netool.censys.bean.search.HostInfo;
import xyz.thewind.music.netool.censys.bean.search.HostService;
import xyz.thewind.music.netool.censys.bean.search.SearchHostResponse;
import xyz.thewind.music.netool.censys.config.CensysConfig;
import xyz.thewind.music.network.util.HttpUtil;
import xyz.thewind.music.util.GsonUtil;
import xyz.thewind.music.util.WLog;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestModel {

    private static final String TAG = "[censys]RequestModel";

    public static void main(String[] args) {
        System.out.println(GsonUtil.toJson(searchJBLicenseServer()));
    }

    public static List<HostInfo> searchHost(String keyword, int num) {
        String url = CensysConfig.SEARCH_HOST_API.replace("_keyword_", URLEncoder.encode(keyword, StandardCharsets.UTF_8)).replace("_num_", num + "");
        String response = HttpUtil.get(url, CensysConfig.headerMap);
        return parseSearchHostResult(response);

    }

    public static List<JBLicense> searchJBLicenseServer() {
        return parseValidSearchResult(searchJBHost());
    }

    public static List<JBLicense> filterDupJBLicense(List<JBLicense> licenses) {
        if (licenses == null) {
            return new ArrayList<>();
        }
        HashMap<String, JBLicense> map = new HashMap<>();
        Iterator<JBLicense> iterator = licenses.listIterator();
        while (iterator.hasNext()) {
            JBLicense license = iterator.next();
            if (map.containsKey(license.serverUrl)) {
                iterator.remove();
            }
            map.put(license.serverUrl, license);
        }
        return licenses;
    }

    public static List<JBLicense> verifyJBLicense(List<JBLicense> licenses, boolean verify) {
        if (licenses == null) {
            return new ArrayList<>();
        }
        licenses = filterDupJBLicense(licenses);
        if (!verify) {
            return licenses;
        }
        final CountDownLatch latch = new CountDownLatch(licenses.size());
        ExecutorService service = Executors.newCachedThreadPool();
        for (final JBLicense jbLicense : licenses) {
            service.submit(() -> {
                String response = HttpUtil.get(jbLicense.getServerUrl() + CensysConfig.JB_VERIFY);
                jbLicense.valid = response.contains("<responseCode>OK</responseCode>");
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (Exception e) {
            WLog.e(TAG, "verifyJBLicense, error, e = " + e);
        }
        return licenses;
    }

    public static List<JBLicense> parseValidSearchResult(List<HostInfo> hostInfos) {
        WLog.d(TAG, "parseValidSearchResult,start");
        final List<JBLicense> licenseServers = new Vector<>();
        if (hostInfos == null) {
            return new ArrayList<>();
        }
        final CountDownLatch latch = new CountDownLatch(hostInfos.size());
        ExecutorService service = Executors.newCachedThreadPool();
        for (HostInfo info : hostInfos) {
            service.submit(() -> {
                String licenseServer = getValidLicenseServerUrl(info);
                if (licenseServer != null) {
                    licenseServers.add(new JBLicense(licenseServer, true));
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (Exception e) {
            WLog.e(TAG, "parseValidSearchResult,error, e = " + e);
        }
        WLog.d(TAG, "parseValidSearchResult,end");
        return filterDupJBLicense(licenseServers);
    }

    public static boolean isJBLicenseValid(String serverUrl) {
        String response = HttpUtil.get(serverUrl + CensysConfig.JB_VERIFY);
        return response.contains("<responseCode>OK</responseCode>");
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
        String response = HttpUtil.get(server.getLicenseServer() + CensysConfig.JB_VERIFY);
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
