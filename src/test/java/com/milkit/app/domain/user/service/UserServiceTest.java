package com.milkit.app.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.milkit.app.config.jwt.JwtTokenProvider;
import com.milkit.app.domain.user.User;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
@Slf4j
class UserServiceTest {

	@Autowired
    private UserServiceImpl userService;

	@AfterEach
	public void end() {
		userService.deleteAll();
	}

	@Test
	@DisplayName("1. 사용자 ID 별 조회 테스트.")
	public void select_test() {
		userService.save(new User("test22", "test22"));
		User userInfo = userService.select("test22");

		assertNotNull(userInfo);
	}

	@Test
	@DisplayName("2. 전체조회_테스트.")
	public void selectAll_test() {
		userService.save(new User("test88", "test88"));
		userService.save(new User("test99", "test99"));
		
		List<User> list = userService.selectAll();

		assertNotNull(list);
	}

	@Test
	@DisplayName("3. ID조회_테스트.")
	public void selectByIds_test() {
		userService.save(new User("test88", "test88"));
		userService.save(new User("test99", "test99"));

		PageRequest pageable = PageRequest.of(0, 10);

		List<User> savedUsers = userService.selectAll();

		List<Long> ids = savedUsers
				.stream()
				.map(e -> e.getId())
				.collect(Collectors.toCollection(ArrayList::new));

		Page<User> list = userService.select(ids, pageable);

		assertNotNull(list);
	}

	@Test
	@DisplayName("4. 등록 테스트.")
	public void save_test() {
		User userInfo = new User("test33", "test33");
		User result = userService.save(userInfo);

		assertNotNull(result);
	}
}
