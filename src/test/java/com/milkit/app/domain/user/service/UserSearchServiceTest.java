package com.milkit.app.domain.user.service;

import com.milkit.app.domain.user.UseYNEnum;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.UserDocument;
import com.milkit.app.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Slf4j
class UserSearchServiceTest {

	@Autowired
	private UserSearchServiceImpl userSearchService;

	private UserRepository userRepository;

	List<User> users;

	@BeforeEach
	public void setup() {
		users = new ArrayList<User>();

		User user1 = User.builder()
				.id(1l)
				.userId("test1@milkit.com")
				.role("ROLE_MEMBER")
				.useYn(UseYNEnum.YES.getValue())
				.description("일반사용자 입니다")
				.build();

		User user2 = User.builder()
				.id(2l)
				.userId("test2@milkit.com")
				.role("ROLE_MEMBER")
				.useYn(UseYNEnum.YES.getValue())
				.description("일반사용자 입니다")
				.build();

		users.add(user1);
		users.add(user2);

		userRepository = mock(UserRepository.class);
		userSearchService.setUserRepository(userRepository);
	}

	@AfterEach
	public void end() {
		userSearchService.deleteAll();
	}

	@Test
	@DisplayName("1. 사용자 전체정보 문서저장 테스트.")
	public void saveAll_test() {
		when(userRepository.findAll()).thenReturn(users);

		List<UserDocument> savedUsers = userSearchService.saveAll();

		List<String> targetUserIds = users.stream().map(u -> u.getUserId()).toList();
		List<String> savedUserIds = savedUsers.stream().map(u -> u.getUserId()).toList();

		assertThat(targetUserIds).isEqualTo(savedUserIds);
	}

	@Test
	@DisplayName("2. 사용자문서 전체검색 테스트.")
	public void findAll_test() {
		when(userRepository.findAll()).thenReturn(users);

		List<UserDocument> saveDocs = userSearchService.saveAll();
		List<UserDocument> findDocs = userSearchService.findAll();

		List<String> savedUserIds = saveDocs.stream().map(u -> u.getUserId()).toList();
		List<String> foundUserIds = findDocs.stream().map(u -> u.getUserId()).toList();

		assertThat(savedUserIds).isEqualTo(foundUserIds);
	}

	@Test
	@DisplayName("3. 사용자문서 조건검색 테스트.")
	public void searchByCondition_test() {
		when(userRepository.findAll()).thenReturn(users);
		userSearchService.saveAll();

		User targetUser = users.get(0);
		String userId = targetUser.getUserId();
		PageRequest pageable = PageRequest.of(0, 10);

		Page<UserDocument> foundDocs = userSearchService.searchByCondition(targetUser, pageable);

		UserDocument matchedDocument = foundDocs.stream().filter(u -> u.getUserId().equals(userId)).findFirst().get();

		assertThat(userId).isEqualTo(matchedDocument.getUserId());
	}
}
