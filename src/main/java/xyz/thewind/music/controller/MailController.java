package xyz.thewind.music.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.model.CommonResp;

@RestController
public class MailController {
    @RequestMapping("/api/mail")
    public ResponseEntity<CommonResp> sendMail(String email){
        CommonResp commonResp=new CommonResp();
        if(sendEmail("【TheWind】","您的验证码是：123456",email)){
            commonResp.code(0).msg("发送成功，请在邮箱查收");
        }
        return new ResponseEntity<>(commonResp, HttpStatus.OK);
    }

    public boolean sendEmail(String subject,String content,String to){
        return true;
    }

}
