package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.Token;
import xyz.thewind.music.model.User;
import xyz.thewind.music.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;
    @RequestMapping("/api/login")
    public ResponseEntity<CommonResp> login(String userId, String password){
        //Token token= TokenHelper.parseToken(request.getHeader("Cookie"));
        User user=userService.findUserByUserIdAndPassword(userId,password);
        HttpHeaders headers=new HttpHeaders();
        if(user!=null){
            headers.add("Set-Cookie",TokenHelper.generateToken(userId,password));
        }
        return new ResponseEntity<>(CommonResp.builder.code(user==null?-1:0).msg("success").obj(user==null?"登陆失败":"登陆成功!"),headers, HttpStatus.OK);
    }
}
