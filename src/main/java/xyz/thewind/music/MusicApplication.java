package xyz.thewind.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import xyz.thewind.music.music.common.MusicConfigManager;
import xyz.thewind.music.util.ApplicationUtils;

@SpringBootApplication
@EnableJpaRepositories("xyz.thewind.music.repository")
public class MusicApplication {
    public static void main(String[] args) {
        ApplicationUtils.CONTEXT = SpringApplication.run(MusicApplication.class, args);
    }

}
