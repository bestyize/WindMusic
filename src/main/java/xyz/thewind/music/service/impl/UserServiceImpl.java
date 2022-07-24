package xyz.thewind.music.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.thewind.music.dto.DTOConverter;
import xyz.thewind.music.dto.UserDTO;
import xyz.thewind.music.model.User;
import xyz.thewind.music.repository.UserRepository;
import xyz.thewind.music.service.UserService;

import java.util.List;


@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean exists(String userId) {
        return userRepository.findTopByUserId(userId) != null;
    }

    @Override
    public boolean existsEmail(String email) {
        return userRepository.findTopByEmail(email) != null;
    }

    @Override
    public User findUserByUserIdAndPassword(String userId, String password) {
        return userRepository.findTopByUserIdAndPassword(userId, password);
    }

    @Override
    public void updateUserStateById(String userId, int state) {
        int status = userRepository.updateStatusByUserId(state, userId);
    }


    @Override
    public User addUser(UserDTO userDTO) {
        User user = DTOConverter.convertUserDtoToUser(userDTO);
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }
}
