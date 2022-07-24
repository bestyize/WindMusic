package xyz.thewind.music;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import xyz.thewind.music.model.User;
import xyz.thewind.music.service.UserService;


@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = {"classpath:application.properties"})
@SpringBootTest
class UserServiceImplTestTest {
    @Autowired
    UserService userService;

    @Test
    public void testFindUserByUserId() {
        User user = userService.findUserByUserIdAndPassword("bestyize", "123456");
        System.out.println(user);
    }

}