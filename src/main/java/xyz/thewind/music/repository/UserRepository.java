package xyz.thewind.music.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.thewind.music.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findTopByUserId(String userId);

    User findTopByEmail(String email);

    User findTopByUserIdAndPassword(String userId, String password);

    @Modifying
    @Query("update User set status= :status where userId = :userId")
    int updateStatusByUserId(@Param("status") int status, @Param("userId") String userId);

    List<User> findAll();
}
