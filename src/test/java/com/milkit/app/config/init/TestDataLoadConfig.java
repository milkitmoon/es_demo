package com.milkit.app.config.init;

import com.milkit.app.domain.user.UseYNEnum;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.repository.UserRepository;
import com.milkit.app.domain.user.service.UserServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.LocalDateTime;

@TestConfiguration
@RequiredArgsConstructor
@Slf4j
public class TestDataLoadConfig {

    private final UserRepository userRepository;
    @PostConstruct
    public void initDB() {
        String password = "$2a$10$1rThoKu6Tt0osRcHVd98A.tiv0./T4tIUQwfRWN3bpkZFQFhf54tq";	//test
        LocalDateTime instTime = LocalDateTime.now();

        User user1 = User.builder()
                .userId("test1@milkit.com")
                .password(password)
                .role("ROLE_MEMBER")
                .useYn(UseYNEnum.YES.getValue())
                .description("일반사용자 입니다")
                .instTime(instTime)
                .updTime(instTime)
                .instUser("admin")
                .build();

        User user2 = User.builder()
                .userId("test2@milkit.com")
                .password(password)
                .role("ROLE_MEMBER")
                .useYn(UseYNEnum.YES.getValue())
                .description("일반사용자 입니다")
                .instTime(instTime)
                .updTime(instTime)
                .instUser("admin")
                .build();

        User admin1 = User
                .builder()
                .userId("admin1@milkit.com")
                .password(password)
                .role("ROLE_ADMIN")
                .useYn(UseYNEnum.YES.getValue())
                .description("관리자 입니다")
                .instTime(instTime)
                .updTime(instTime)
                .instUser("admin")
                .build();

        User admin2 = User.builder()
                .userId("admin2@milkit.com")
                .password(password)
                .role("ROLE_ADMIN")
                .useYn(UseYNEnum.YES.getValue())
                .description("관리자 입니다")
                .instTime(instTime)
                .updTime(instTime)
                .instUser("admin")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(admin1);
        userRepository.save(admin2);
    }

    @PreDestroy
    public void preDestroy() {
        userRepository.deleteAll();
    }
}
