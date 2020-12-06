package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.config.CommonConfig;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.Token;
import xyz.thewind.music.model.User;
import xyz.thewind.music.service.UserService;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
            commonResp.code(0).msg("登陆成功").obj(user);
            Cookie inf=new Cookie("info", Base64Converter.encode(GsonConverter.toJson(user)));
            inf.setPath("/");
            Cookie tok=new Cookie("token",TokenHelper.generateToken(userId,password));
            tok.setPath("/");
            response.addCookie(inf);
            response.addCookie(tok);
        }
        return new ResponseEntity<>(commonResp,headers, HttpStatus.OK);
    }
}
