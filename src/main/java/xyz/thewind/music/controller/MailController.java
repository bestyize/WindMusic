package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.Token;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.service.UserService;
import xyz.thewind.music.service.impl.RedisServiceImpl;
import xyz.thewind.music.util.VerifyCode;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class MailController {
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
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
