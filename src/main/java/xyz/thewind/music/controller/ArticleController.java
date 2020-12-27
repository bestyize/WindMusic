package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.dto.ArticleDTO;
import xyz.thewind.music.dto.DTOConverter;
import xyz.thewind.music.model.Article;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.Token;
import xyz.thewind.music.service.ArticleService;
import xyz.thewind.music.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;

    @RequestMapping("/post")
    public ResponseEntity<CommonResp> postArticle(String data, HttpServletRequest request){
        Token token= TokenHelper.parseToken(request.getCookies());
        CommonResp commonResp=new CommonResp();
        if(!token.userId.isEmpty()&&userService.findUserByUserIdAndPassword(token.userId,token.password)!=null){
            Article article=articleService.addArticle(DTOConverter.defaultArticleDTO(data,token.userId));
            commonResp.code(0).msg("成功").obj(article);
        }
        return new ResponseEntity<>(commonResp, HttpStatus.OK);
    }
    @RequestMapping("/get")
    public ResponseEntity<CommonResp> getArticle(String id,HttpServletRequest request){
        Token token= TokenHelper.parseToken(request.getCookies());
        CommonResp commonResp=new CommonResp();
        Article article=articleService.findArticleByArticleId(id);
        commonResp.code(0).msg("成功").obj(article);
        return new ResponseEntity<>(commonResp, HttpStatus.OK);
    }
    @RequestMapping("/getbyuserid")
    public ResponseEntity<CommonResp> getArticleByUserId(String userId,HttpServletRequest request){
        Token token= TokenHelper.parseToken(request.getCookies());
        CommonResp commonResp=new CommonResp();
        List<Article> article=articleService.findArticleByUserId(userId);
        commonResp.code(0).msg("成功").obj(article);
        return new ResponseEntity<>(commonResp, HttpStatus.OK);
    }


}
