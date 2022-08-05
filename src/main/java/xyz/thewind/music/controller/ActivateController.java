package xyz.thewind.music.controller;

import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.netool.censys.bean.JBLicense;
import xyz.thewind.music.netool.censys.model.RequestModel;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.ApplicationUtils;
import xyz.thewind.music.util.GsonUtil;
import xyz.thewind.music.util.PropertyReader;

import java.util.*;

@RestController
public class ActivateController {

    private static final String REDIS_KEY_JB_SERVER_LIST = "jetbrain_licenses";

    @RequestMapping("/activate/api/jetbrain/getLicenseServers")
    public ResponseEntity<CommonResp> requestJBLicenseServer() {
        CommonResp commonResp = new CommonResp();
        commonResp.code(0).msg("OK");
        RedisService service = getRedisService();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (service != null){
            String licenses = service.get(REDIS_KEY_JB_SERVER_LIST);
            List<JBLicense> parseList = GsonUtil.fromJson(licenses, new TypeToken<ArrayList<JBLicense>>(){}.getType());
            commonResp.obj = parseList == null?new ArrayList<>():parseList;
        }
        return new ResponseEntity<>(commonResp, headers, HttpStatus.OK);

    }

    @RequestMapping("/activate/api/jetbrain/addLicenseServer")
    public ResponseEntity<CommonResp> addJBLicenseServer(String serverUrl) {
        CommonResp commonResp = new CommonResp();
        commonResp.code(0);
        RedisService service = getRedisService();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (service != null){
            String licenses = service.get(REDIS_KEY_JB_SERVER_LIST);
            List<JBLicense> parseList = GsonUtil.fromJson(licenses, new TypeToken<ArrayList<JBLicense>>(){}.getType());
            if (parseList == null) {
                parseList = new ArrayList<>();
            }
            parseList.add(new JBLicense(serverUrl, RequestModel.isJBLicenseValid(serverUrl)));
            service.set(REDIS_KEY_JB_SERVER_LIST, GsonUtil.toJson(parseList));
            commonResp.msg("add successful");
        }
        return new ResponseEntity<>(commonResp, headers, HttpStatus.OK);

    }

    @RequestMapping("/activate/api/jetbrain/searchLicenseServer")
    public ResponseEntity<CommonResp> searchJBLicenseServer() {
        CommonResp commonResp = new CommonResp();
        RedisService service = getRedisService();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        commonResp.code(0).msg("OK");
        if (service != null){
            List<JBLicense> licenseServers = RequestModel.searchJBLicenseServer();
            String licenses = service.get(REDIS_KEY_JB_SERVER_LIST);
            List<JBLicense> parseList = GsonUtil.fromJson(licenses, new TypeToken<ArrayList<JBLicense>>(){}.getType());
            if (parseList == null) {
                parseList = new ArrayList<>();
            }
            parseList.addAll(licenseServers);
            service.set(REDIS_KEY_JB_SERVER_LIST, GsonUtil.toJson(parseList));
        }
        return new ResponseEntity<>(commonResp, headers, HttpStatus.OK);
    }

    @RequestMapping("/activate/api/jetbrain/updateLicenseServer")
    public ResponseEntity<CommonResp> updateJBLicenseServer(boolean verify) {
        CommonResp commonResp = new CommonResp();
        RedisService service = getRedisService();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        commonResp.code(0);
        if (service != null){
            String licenses = service.get(REDIS_KEY_JB_SERVER_LIST);
            List<JBLicense> parseList = GsonUtil.fromJson(licenses, new TypeToken<ArrayList<JBLicense>>(){}.getType());
            parseList = RequestModel.verifyJBLicense(parseList, verify);
            service.set(REDIS_KEY_JB_SERVER_LIST, GsonUtil.toJson(parseList));
            commonResp.obj = parseList;
        }
        return new ResponseEntity<>(commonResp, headers, HttpStatus.OK);
    }

    public RedisService getRedisService() {
        RedisService service = ApplicationUtils.CONTEXT.getAutowireCapableBeanFactory().getBean(RedisService.class);
        if (!service.isRedisActive()) {
            return null;
        }
        return service;
    }
}
