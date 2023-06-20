package com.milkit.app.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import com.milkit.app.common.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.milkit.app.config.jwt.JwtTokenProvider;
import com.milkit.app.domain.user.User;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@Slf4j
class UserServiceTest {

	@Autowired
    private UserServiceImpl userInfoService;
	
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

	@Test
	@DisplayName("1. 사용자 ID 별 조회 테스트.")
	public void select_test() {
		userInfoService.insert(new User("test22", "test22"));
		User userInfo = userInfoService.select("test22");

		assertNotNull(userInfo);
	}

	@Test
	@DisplayName("2. 전체조회_테스트.")
	public void selectAll_test() {
		userInfoService.insert(new User("test88", "test88"));
		userInfoService.insert(new User("test99", "test99"));
		
		List<User> list = userInfoService.selectAll();

		assertNotNull(list);
	}

	@Test
	@DisplayName("3. 등록 테스트.")
	public void insert_test() {
		User userInfo = new User("test33", "test33");
		User result = userInfoService.insert(userInfo);

		assertNotNull(result);
	}

	@Test
	@DisplayName("4. 회원가입 테스트.")
	public void signup_test() {
		User user = User
		.builder()
		.userId("signup@milkit.com")
		.password("test")
		.role("ROLE_MEMBER")
		.build();
		
		User result = userInfoService.signup(user);

		assertNotNull(result);
	}

	@Test
	@DisplayName("5. 잘못된 회원정보형식으로 회원가입을 요청했을 시 예외를 테스트")
	public void signup_user_info_exception_test() {
		User user = User
				.builder()
				.userId("testUser")
				.password("test")
				.role("ROLE_MEMBER")
				.build();

		assertThatThrownBy(() -> userInfoService.signup(user) )
				.isInstanceOf(ServiceException.class)
				.hasMessageContaining("이메일 형식이 아닙니다. 입력정보를 확인해 주세요");
	}
}
