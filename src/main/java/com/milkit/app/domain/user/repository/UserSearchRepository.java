package com.milkit.app.domain.user.repository;

import com.milkit.app.domain.user.UserDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserSearchRepository extends ElasticsearchRepository<UserDocument,Long> {

    UserDocument findByUserId(String userId);

    Page<UserDocument> findByRole(String role, Pageable pageable);

    Page<UserDocument> findByUseYn(String useYn, Pageable pageable);
}
