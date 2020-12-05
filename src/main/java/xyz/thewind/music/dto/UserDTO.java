package xyz.thewind.music.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String userId;
    private String password;
    private String userName;
    private String email;
    private String mobile;
    private Date birthday;
    private Integer level;
    private Integer status;
    private String avatar;
}
