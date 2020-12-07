package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;

import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.User;
import xyz.thewind.music.service.UserService;

import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;
    @RequestMapping("/api/login")
    public ResponseEntity<CommonResp> login(String userId, String password, HttpServletResponse response){
        CommonResp commonResp=new CommonResp();
        User user=userService.findUserByUserIdAndPassword(userId,password);
        HttpHeaders headers=new HttpHeaders();
        if(user!=null){
            user.setPassword("");
            TokenHelper.setCookie(response,user,userId,password);
            commonResp.code(0).msg("登陆成功").obj(user);

        }
        return new ResponseEntity<>(commonResp,headers, HttpStatus.OK);
    }
}
