package xyz.thewind.music.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class EdrawActivateController {
    private static final String edrawVipInfo = "{\"status\":\"success\",\"data\":{\"user_id\":88888888,\"user_name\":\"xxxxxx\",\"email\":null,\"mobile\":\"xxxxxx\",\"nick_name\":\"\\u4ea6\\u6cfd\",\"max_storage\":768435456,\"used_storage\":31757,\"activation\":3,\"buy_deadtime\":2604717619,\"reg_time\":1582201220,\"lang\":\"\",\"profile\":\"\",\"birthday\":\"1900-01-01 00:00:00\",\"country\":\"\",\"region\":\"\",\"sex\":\"M\",\"inviter\":0,\"avatar_url\":\"https:\\/\\/edrawcloudpubliccn.oss-cn-shenzhen.aliyuncs.com\\/profile\\/88888888\\/avatar.jpg\",\"avatar\":\"\",\"migrate\":null,\"product\":\"max-pc\",\"channel\":\"offical\",\"ban\":0,\"point_storage\":0,\"max_buy_storage\":80000000,\"master_buy_storage\":80000000,\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJFZHJhd1NvZnQiLCJhdWQiOjE4NTU1MDkwLCJpYXQiOjE2MDUwMTY0NzIsImV4cCI6MTYwNzYwODQ3MiwiZnJvbSI6Im1heC1wYyJ9.5JtDfYVVAJnTkrtOodr5P8yGRxA6uwfRh6B6Kv7hdhk\",\"_current\":1605016472,\"login_type\":\"mobile_pw\"},\"msg\":null}";

    //    @RequestMapping({"/a/ed/cn/api/user/*","/ed/com/api/user/*","/mmact/cn/api/user/*","/mmact/com/api/user/*"})
    @RequestMapping({"/a/ed/cn/api/user/{id}/{product}/check", "/a/ed/com/api/user/{id}/{product}/check", "/a/mmact/cn/api/user/{id}/{product}/check", "/a/mmact/com/api/user/{id}/{product}/check"})
    public ResponseEntity<String> edrawActivate(@PathVariable(value = "id") String id, @PathVariable String product) {
        return new ResponseEntity<>(edrawVipInfo.replaceAll("88888888", id), HttpStatus.OK);
    }
}
