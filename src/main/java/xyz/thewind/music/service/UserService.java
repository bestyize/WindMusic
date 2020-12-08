package xyz.thewind.music.service;

import xyz.thewind.music.dto.UserDTO;
import xyz.thewind.music.model.User;

public interface UserService {
    boolean exists(String userId);
    boolean existsEmail(String email);
    User findUserByUserIdAndPassword(String userId,String password);
    void updateUserStateById(String userId, int state);
    User addUser(UserDTO userDTO);
}
