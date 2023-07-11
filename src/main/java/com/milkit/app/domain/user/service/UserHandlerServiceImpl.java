package com.milkit.app.domain.user.service;

import com.milkit.app.common.AppCommon;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.config.jwt.JwtToken;
import com.milkit.app.config.jwt.JwtTokenProvider;
import com.milkit.app.domain.user.RoleEnum;
import com.milkit.app.domain.user.UseYNEnum;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.UserDocument;
import com.milkit.app.domain.user.repository.UserRepository;
import com.milkit.app.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserHandlerServiceImpl {

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private UserSearchServiceImpl userSearchService;

	@Autowired
	private UserRepository userRepository;
	
    @Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void setUserSearchService(UserSearchServiceImpl userSearchService) {
		this.userSearchService = userSearchService;
	}

	public List<User> selectAll() {
		return userService.selectAll();
	}

	public User select(final Long id) {
	    return userService.select(id);
	}
	
	public User select(final String userId) {
	    return userService.select(userId);
	}

    public User save(final User member) {
		User user = userService.save(member);
		userSearchService.save(user);

		return user;
    }

	@Transactional
	public List<User> saveAll(List<User> users) {
		List<User> savedUsers = userService.saveAll(users);
		userSearchService.saveAll(savedUsers);

		return savedUsers;
	}

	public Page<User> findByRole(String role, Pageable pageable) {
		Page<UserDocument> userDocuments = userSearchService.findByRole(role, pageable);

		return searchByDocuments(userDocuments, pageable);
	}

	public Page<User> findByUseYn(String useYn, Pageable pageable) {
		Page<UserDocument> userDocuments = userSearchService.findByUseYn(useYn, pageable);

		return searchByDocuments(userDocuments, pageable);
	}

	public Page<User> searchByCondition(User user, Pageable pageable) {
		Page<UserDocument> userDocuments = userSearchService.searchByCondition(user, pageable);

		return searchByDocuments(userDocuments, pageable);
	}

	public Page<User> searchByDocuments(Page<UserDocument> userDocuments, Pageable pageable) {
		List<Long> ids = userDocuments
				.stream()
				.map(e -> e.getId())
				.collect(Collectors.toCollection(ArrayList::new));

		return userService.select(ids, pageable);
	}

	@Transactional
	public void deleteAll() {
		userService.deleteAll();
		userSearchService.deleteAll();
	}

	public User signup(User user) {

		if(StringUtils.isBlank(user.getUserId())
			|| StringUtils.isBlank(user.getPassword())
			|| StringUtils.isBlank(user.getRole())
		) {
			throw new ServiceException(ErrorCodeEnum.InvalidSignUpParameterException.getCode(), new String[]{user.getUserId(), user.getPassword(), user.getRole()});
		}

		if( !StringUtil.isValidEmail(user.getUserId()) ) {
			throw new ServiceException(ErrorCodeEnum.InvalidEmailFormException.getCode(), new String[]{user.getUserId()});
		}

		if( !user.getRole().equals(RoleEnum.MEMBER.getValue())
			&& !user.getRole().equals(RoleEnum.ADMIN.getValue())
		) {
			throw new ServiceException(ErrorCodeEnum.InvalidRoleException.getCode(), new String[]{user.getRole()});
		}

		User existUser = select(user.getUserId());
		if(existUser != null) {
			throw new ServiceException(ErrorCodeEnum.ExistUserException.getCode(), new String[]{user.getUserId()});
		}

		LocalDateTime instTime = LocalDateTime.now();
		user.setInstUser(user.getUserId());
		user.setUpdUser(user.getUserId());
		user.setInstTime(instTime);
		user.setUpdTime(instTime);
		user.setUseYn(UseYNEnum.YES.getValue());
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return save(user);
	}
	
	public JwtToken refresh(String token) throws Exception {
		String userId = jwtTokenProvider.getUsername(token.replace(AppCommon.JWT_TOKEN_PREFIX, ""));
		
		return jwtTokenProvider.createBody( select(userId) );
	}
}
