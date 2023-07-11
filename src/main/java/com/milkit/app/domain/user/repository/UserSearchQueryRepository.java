package com.milkit.app.domain.user.repository;

import com.milkit.app.domain.user.UserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserSearchQueryRepository {

    private final ElasticsearchOperations operations;

    public SearchPage<UserDocument> findByCondition(UserDocument userDocument, Pageable pageable) {
        CriteriaQuery query = createConditionQuery(userDocument).setPageable(pageable);

        SearchHits<UserDocument> searchHits = operations.search(query, UserDocument.class);

        return SearchHitSupport.searchPageFor(searchHits, pageable);
    }

    public Page<Long> findIdsByCondition(UserDocument userDocument, Pageable pageable) {
        CriteriaQuery query = createConditionQuery(userDocument).setPageable(pageable);

        SearchHits<UserDocument> searchHits = operations.search(query, UserDocument.class);

        return searchPageForIds(searchHits, pageable);
    }

    private Page<Long> searchPageForIds(SearchHits<UserDocument> searchHits, Pageable pageable) {
        List<Long> documentIds = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            documentIds.add(Long.parseLong(hit.getId()));
        }

        return new PageImpl<>(documentIds, pageable, searchHits.getTotalHits());
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
            query.addCriteria(Criteria.where("description").matches(userDocument.getDescription()));
        }

        return query;
    }
}
