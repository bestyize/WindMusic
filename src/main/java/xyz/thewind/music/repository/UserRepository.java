package xyz.thewind.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.thewind.music.model.User;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User findTopByUserId(String userId);
    User findTopByEmail(String email);
    User findTopByUserIdAndPassword(String userId,String password);
}
