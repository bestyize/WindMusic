package xyz.thewind.music.netool.censys.config;

import java.util.HashMap;
import java.util.Map;

public class CensysConfig {

    public static final String AUTH_HEADER = "authorization";
    public static final String AUTH_VAL = "Basic NmNhNDc5OWMtNmUwZi00NzhjLWJiZDUtYzQzZTA5NzgyNDBmOmdMT2VOM1pHVUVqcjJ4UXJ0MTRYN2JVVVM0dnJwdmxL";

    public static final Map<String, String> headerMap = new HashMap<>() {{
        put(AUTH_HEADER, AUTH_VAL);
    }};

    public static final String JB_HOST = "services.http.response.headers.location:account.jetbrains.com/fls-auth";

    public static String SEARCH_HOST_API = "https://search.censys.io/api/v2/hosts/search?q=_keyword_&per_page=_num_&virtual_hosts=EXCLUDE";

    public static String JB_VERIFY = "/rpc/obtainTicket.action?buildDate=20220412&buildNumber=2022.1.4+Build+IU-221.6008.13&clientVersion=13&hostName=readzhangdeiMac.local&machineId=8639d40a-3822-40ac-8121-45b36cd3e996&productCode=49c202d4-ac56-452b-bb84-735056242fb3&productFamilyId=49c202d4-ac56-452b-bb84-735056242fb3&salt=" + System.currentTimeMillis() + "&secure=false&userName=yize&version=2022100&versionNumber=2022100";



}
