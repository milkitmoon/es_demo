package com.milkit.app.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

import com.milkit.app.common.AppCommon;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.config.jwt.JwtToken;
import com.milkit.app.config.jwt.JwtTokenProvider;
import com.milkit.app.domain.user.RoleEnum;
import com.milkit.app.domain.user.UseYNEnum;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.repository.UserRepository;
import com.milkit.app.util.StringUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class UserServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
    @Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> selectAll() {
		return userRepository.findAll();
	}

	public User select(final Long id) {
	    User userInfo = null;
	
	    Optional<User> optionalPost = userRepository.findById(id);
	    if(optionalPost.isPresent()) {
	    	userInfo = optionalPost.get();
	    }
	
	    return userInfo;
	}
	
	public User select(final String userId) {
	    return userRepository.findByUserId(userId);
	}

    public User insert(final User member) {
    	return userRepository.save(member);
    }

	@Transactional
	public void saveAll(List<User> users) {
		userRepository.saveAll(users);
	}

	@Transactional
	public void deleteAll() {
		userRepository.deleteAll();
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return select(userId);
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

		return insert(user);
	}
	
	public JwtToken refresh(String token) throws Exception {
		String userId = jwtTokenProvider.getUsername(token.replace(AppCommon.JWT_TOKEN_PREFIX, ""));
		
		return jwtTokenProvider.createBody( select(userId) );
	}
}
