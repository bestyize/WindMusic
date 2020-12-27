package xyz.thewind.music.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.Token;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @RequestMapping("/post")
    public ResponseEntity<CommonResp> postArticle(String article, HttpServletRequest request){
        Token token= TokenHelper.parseToken(request.getCookies());

        return null;
    }
}
