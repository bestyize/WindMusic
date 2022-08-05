package xyz.thewind.music.dto;


import xyz.thewind.music.model.User;
import xyz.thewind.music.util.GsonUtil;

import java.util.Date;
import java.util.UUID;

public class DTOConverter {
    public static User convertUserDtoToUser(UserDTO userDTO) {
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

    public static UserDTO defaultUserDTO(String userId, String password, String email, String userName) {
        return new UserDTO(userId, password, userName, email, null, new Date(), 1, 1, "/img/person.png");
    }

    public static ArticleDTO defaultArticleDTO(String data, String userId) {
        ArticleDTO articleDTO = new ArticleDTO();
        try {
            articleDTO = GsonUtil.fromJson(data, ArticleDTO.class);
            articleDTO.setUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleDTO;
    }
}
