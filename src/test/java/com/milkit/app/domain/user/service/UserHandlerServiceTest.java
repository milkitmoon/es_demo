package com.milkit.app.domain.user.service;

import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.user.UseYNEnum;
import com.milkit.app.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
class UserHandlerServiceTest {

	@Autowired
    private UserHandlerServiceImpl userHandlerService;

	@AfterEach
	public void end() {
		userHandlerService.deleteAll();
	}

	@Test
	@DisplayName("1. 사용자 ID 별 조회 테스트.")
	public void select_test() {
		userHandlerService.save(new User("test22", "test22"));
		User userInfo = userHandlerService.select("test22");

		assertNotNull(userInfo);
	}

	@Test
	@DisplayName("2. 전체조회_테스트.")
	public void selectAll_test() {
		userHandlerService.save(new User("test88", "test88"));
		userHandlerService.save(new User("test99", "test99"));
		
		List<User> list = userHandlerService.selectAll();

		assertNotNull(list);
	}

	@Test
	@DisplayName("3. 등록 테스트.")
	public void save_test() {
		User userInfo = new User("test33", "test33");
		User result = userHandlerService.save(userInfo);

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
		
		User result = userHandlerService.signup(user);

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

		assertThatThrownBy(() -> userHandlerService.signup(user) )
				.isInstanceOf(ServiceException.class)
				.hasMessageContaining("이메일 형식이 아닙니다. 입력정보를 확인해 주세요");
	}

	@Test
	@DisplayName("6. 사용자문서 조건검색 테스트.")
	public void searchByCondition_test() {
		List<User> users = new ArrayList<>();

		User user1 = User.builder()
				.userId("test1@milkit.com")
				.role("ROLE_MEMBER")
				.useYn(UseYNEnum.YES.getValue())
				.description("일반사용자 입니다")
				.build();

		User user2 = User.builder()
				.userId("test2@milkit.com")
				.role("ROLE_MEMBER")
				.useYn(UseYNEnum.YES.getValue())
				.description("일반사용자 입니다")
				.build();

		users.add(user1);
		users.add(user2);

		userHandlerService.saveAll(users);

		User targetUser = users.get(0);
		String userId = targetUser.getUserId();
		PageRequest pageable = PageRequest.of(0, 10);

		Page<User> foundUsers = userHandlerService.searchByCondition(targetUser, pageable);

		User matchedUser = foundUsers.stream().filter(u -> u.getUserId().equals(userId)).findFirst().get();

		assertThat(userId).isEqualTo(matchedUser.getUserId());
	}
}
