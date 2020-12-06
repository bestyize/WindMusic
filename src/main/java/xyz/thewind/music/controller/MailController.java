package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.config.CommonConfig;
import xyz.thewind.music.model.CommonResp;

@RestController
public class MailController {
    @Autowired
    private JavaMailSender mailSender;
    @RequestMapping("/api/mail")
    public ResponseEntity<CommonResp> sendMail(String email){
        CommonResp commonResp=new CommonResp();
        if(sendEmail("【TheWind】","您的验证码是：123456",email)){
            commonResp.code(0).msg("发送成功，请在邮箱查收");
        }
        return new ResponseEntity<>(commonResp, HttpStatus.OK);
    }

    public boolean sendEmail(String subject,String content,String to){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 设置收件人，寄件人
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom("windreg@163.com");
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        // 发送邮件
        try {
            mailSender.send(simpleMailMessage);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

}
