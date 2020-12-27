package xyz.thewind.music.dto;

import xyz.thewind.music.model.Article;
import xyz.thewind.music.model.User;
import xyz.thewind.music.util.GsonConverter;

import java.util.Date;
import java.util.UUID;

public class DTOConverter {
    public static User convertUserDtoToUser(UserDTO userDTO){
        return new User(userDTO.getUserId(),
                userDTO.getPassword(),
                userDTO.getUserName(),
                userDTO.getEmail(),
                userDTO.getMobile(),
                userDTO.getBirthday(),
                new Date(),
                userDTO.getLevel(),
                userDTO.getStatus(),
                userDTO.getAvatar());
    }

    public static UserDTO defaultUserDTO(String userId,String password,String email,String userName){
        return new UserDTO(userId,password,userName,email,null,new Date(),1,1,"/img/person.png");
    }

    public static Article convertArticleDtoToArticle(ArticleDTO articleDTO){
        String articleId=UUID.randomUUID().toString().replace("-","");
        Article article=new Article(articleId,
                articleDTO.getArticleTitle(),
                articleDTO.getArticleContent(),
                articleId,
                0,
                new Date(),
                articleDTO.getCoverUrl(),
                articleDTO.getUserId(),
                0);
        return article;
    }

    public static ArticleDTO defaultArticleDTO(String data,String userId){
        ArticleDTO articleDTO=new ArticleDTO();
        try {
            articleDTO= GsonConverter.fromJson(data,ArticleDTO.class);
            articleDTO.setUserId(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return articleDTO;
    }
}
