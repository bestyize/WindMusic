package xyz.thewind.music.service;

import xyz.thewind.music.dto.UserDTO;
import xyz.thewind.music.model.User;

import java.util.List;

public interface UserService {
    boolean exists(String userId);
    boolean existsEmail(String email);
    User findUserByUserIdAndPassword(String userId,String password);
    void updateUserStateById(String userId, int state);
    User addUser(UserDTO userDTO);
    List<User> findAllUser();
}
