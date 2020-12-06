package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.dto.DTOConverter;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.User;
import xyz.thewind.music.service.UserService;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.StringUtils;


@RestController
public class RegController {
    @Autowired
    private UserService userService;
    @RequestMapping("/api/reg")
    public ResponseEntity<CommonResp> regAccount(String userId,String password,String email,String userName){
        CommonResp commonResp=new CommonResp();
        HttpHeaders headers=new HttpHeaders();
        if(!StringUtils.isCorrectUserId(userId)||!StringUtils.isCorrectPassword(password)||!StringUtils.isCorrectEmail(email)||!StringUtils.isCorrectUserName(userName)){
            commonResp.code(-1).msg("注册失败，注册信息有误");
        }else if(userService.exists(userId)){
            commonResp.code(-1).msg("注册失败，已有该用户名");
        }else if(userService.existsEmail(email)){
            commonResp.code(-1).msg("注册失败，已有该邮箱");
        } else {
            try {
                User user=userService.addUser(DTOConverter.defaultUserDTO(userId,password,email,userName));
                headers.add("Set-Cookie",TokenHelper.generateToken(user.getUserId(),user.getPassword()));
                user.setPassword("");
                headers.add("Set-Cookie","info="+ GsonConverter.toJson(user));
                commonResp.code(0).msg("注册成功").obj(user);
            }catch (Exception e){
                commonResp.code(-1).msg("注册失败:"+e.toString());
            }
        }
        return new ResponseEntity<>(commonResp,headers, HttpStatus.OK);
    }
}
