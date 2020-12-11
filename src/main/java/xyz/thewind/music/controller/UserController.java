package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.dto.DTOConverter;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.Token;
import xyz.thewind.music.model.User;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.service.UserService;
import xyz.thewind.music.util.StringUtils;
import xyz.thewind.music.util.VerifyCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @RequestMapping("/api/reg")
    public ResponseEntity<CommonResp> regAccount(String userId, String password, String email, String userName, HttpServletResponse response){
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
                user.setPassword("");
                TokenHelper.setCookie(response,user,userId,password);
                commonResp.code(0).msg("注册成功").obj(user);
            }catch (Exception e){
                commonResp.code(-1).msg("注册失败:"+e.toString());
            }
        }
        return new ResponseEntity<>(commonResp,headers, HttpStatus.OK);
    }

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

    @RequestMapping("/api/mail")
    public ResponseEntity<CommonResp> sendMail(HttpServletRequest request){
        String email=request.getParameter("email");
        String userId=request.getParameter("userId");
        CommonResp commonResp=new CommonResp();
        if(!redisService.isRedisActive()){
            commonResp.code(-1).msg("验证服务尚未开启,请稍后再试");

        }else {
            String verifyCode= VerifyCode.generateVerifyCode(4);
            if(sendEmail("【TheWind验证】","您的验证码是："+verifyCode+",有效期10分钟",email)){
                commonResp.code(0).msg("发送成功，请在邮箱查收，如果找不到，请去垃圾邮箱查看");
                String key=email+userId;
                redisService.set(key,verifyCode);
                redisService.setExpire(key,600);

            }
        }

        return new ResponseEntity<>(commonResp, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/verifycode")
    public ResponseEntity<CommonResp> verifyCode(HttpServletRequest request){
        Token token= TokenHelper.parseToken(request.getCookies());
        CommonResp commonResp=new CommonResp();
        if(!redisService.isRedisActive()){
            commonResp.code(-1).msg("验证服务尚未开启,请稍后再试");
        }else {
            String email=request.getParameter("email");
            String userId=request.getParameter("userId");
            String code=request.getParameter("code");
            String key=email+userId;
            String verifyCode=redisService.get(key);
            if(verifyCode!=null&&verifyCode.equals(code)){
                if(userService.findUserByUserIdAndPassword(token.getUserId(),token.getPassword())!=null){
                    userService.updateUserStateById(token.getUserId(),2);
                    redisService.remove(key);//验证完成后移除
                    commonResp.code(0).msg("验证成功！");
                }else {
                    commonResp.code(0).msg("验证失败，该用户未注册！");
                }

            }
        }
        return new ResponseEntity<>(commonResp,HttpStatus.OK);
    }

    @RequestMapping("/api/queryalluser")
    public ResponseEntity<List<User>> findAllUser(HttpServletRequest request){
        Token token=TokenHelper.parseToken(request.getCookies());
        User user=userService.findUserByUserIdAndPassword(token.userId, token.password);
        List<User> userList=new ArrayList<>();
        if(user!=null&&user.getLevel()>=7){
            userList= userService.findAllUser();
        }
        return new ResponseEntity<>(userList,HttpStatus.OK);
    }

    public boolean sendEmail(String subject,String content,String to){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setFrom("windreg@163.com");
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        try {
            mailSender.send(msg);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
