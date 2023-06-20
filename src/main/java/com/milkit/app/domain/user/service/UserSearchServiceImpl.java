package com.milkit.app.domain.user.service;

import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.UserDocument;
import com.milkit.app.domain.user.repository.UserRepository;
import com.milkit.app.domain.user.repository.UserSearchQueryRepository;
import com.milkit.app.domain.user.repository.UserSearchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserSearchServiceImpl {

	private UserRepository userRepository;
	private UserSearchRepository userSearchRepository;
	private UserSearchQueryRepository userSearchQueryRepository;


	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	public void setUserSearchRepository(UserSearchRepository userSearchRepository) {
		this.userSearchRepository = userSearchRepository;
	}

	@Autowired
	public void setUserSearchRepository(UserSearchQueryRepository userSearchQueryRepository) {
		this.userSearchQueryRepository = userSearchQueryRepository;
	}

	@Transactional
	public List<UserDocument> saveAll() {
		List<User> users = userRepository.findAll();
		List<UserDocument> userDocumentList
				= users.stream().map(UserDocument::from).collect(Collectors.toList());
		Iterable<UserDocument> result = userSearchRepository.saveAll(userDocumentList);

		return iterableToList(result);
	}

	public List<UserDocument> findAll() {
		Iterable<UserDocument> result = userSearchRepository.findAll();

		return iterableToList(result);
	}

	public UserDocument findByUserId(String userId) {
		return userSearchRepository.findByUserId(userId);
	}

	public Page<UserDocument> findByRole(String role, Pageable pageable) {
		return userSearchRepository.findByRole(role, pageable);
	}

	public Page<UserDocument> findByUseYn(String useYn, Pageable pageable) {
		return userSearchRepository.findByUseYn(useYn, pageable);
	}

	public Page<UserDocument> searchByCondition(UserDocument userDocument, Pageable pageable) {
		return (Page<UserDocument>)SearchHitSupport.unwrapSearchHits(userSearchQueryRepository.findByCondition(userDocument, pageable));
	}

	public void deleteAll() {
		userSearchRepository.deleteAll();
	}

	private <T> List<T> iterableToList(Iterable<T> ite) {
		return StreamSupport.stream(ite.spliterator(), false).collect(Collectors.toList());
	}
}
