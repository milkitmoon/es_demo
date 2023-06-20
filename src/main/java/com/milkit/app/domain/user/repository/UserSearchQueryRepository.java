package com.milkit.app.domain.user.repository;

import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.UserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserSearchQueryRepository {

    private final ElasticsearchOperations operations;

    public SearchPage<UserDocument> findByCondition(UserDocument userDocument, Pageable pageable) {
        CriteriaQuery query = createConditionQuery(userDocument).setPageable(pageable);

        SearchHits<UserDocument> searchHits = operations.search(query, UserDocument.class);

        return SearchHitSupport.searchPageFor(searchHits, pageable);
    }

    private CriteriaQuery createConditionQuery(UserDocument userDocument) {
        CriteriaQuery query = new CriteriaQuery(new Criteria());

        if (userDocument == null) {
            return query;
        }

        if (userDocument.getId() != null) {
            query.addCriteria(Criteria.where("id").is(userDocument.getId()));
        }

        if(StringUtils.hasText(userDocument.getUserId())) {
            query.addCriteria(Criteria.where("userId").is(userDocument.getUserId()));
        }

        if(userDocument.getRole() != null) {
            query.addCriteria(Criteria.where("role").is(userDocument.getRole()));
        }

        if(userDocument.getUseYn() != null) {
            query.addCriteria(Criteria.where("useYn").is(userDocument.getUseYn()));
        }

        if(StringUtils.hasText(userDocument.getDescription())) {
            query.addCriteria(Criteria.where("description").is(userDocument.getDescription()));
        }

        return query;
    }
}
