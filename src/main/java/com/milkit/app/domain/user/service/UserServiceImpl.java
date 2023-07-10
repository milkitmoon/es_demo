package com.milkit.app.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.milkit.app.config.jwt.JwtTokenProvider;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.repository.UserRepository;

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


	public Page<User> select(final List<Long> ids, Pageable pageable) {
		return userRepository.findByIdIn(ids, pageable);
	}

    public User save(final User member) {
    	return userRepository.save(member);
    }

	@Transactional
	public List<User> saveAll(List<User> users) {
		return userRepository.saveAll(users);
	}

	@Transactional
	public void deleteAll() {
		userRepository.deleteAll();
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return select(userId);
	}
}
