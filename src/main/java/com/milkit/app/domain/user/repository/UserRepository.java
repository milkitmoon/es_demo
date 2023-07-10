package com.milkit.app.domain.user.repository;

import com.milkit.app.domain.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserId(String userId);

	Page<User> findByIdIn(List<Long> ids, Pageable pageable);
}
