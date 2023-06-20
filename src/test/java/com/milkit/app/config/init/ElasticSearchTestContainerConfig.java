package com.milkit.app.config.init;

import com.milkit.app.domain.user.repository.UserSearchQueryRepository;
import com.milkit.app.domain.user.repository.UserSearchRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@TestConfiguration
@EnableElasticsearchRepositories(basePackageClasses = { UserSearchRepository.class, UserSearchQueryRepository.class })
public class ElasticSearchTestContainerConfig extends ElasticsearchContainer {
    private static final String DOCKER_ELASTIC = "docker.elastic.co/elasticsearch/elasticsearch:7.17.6";

    private static final String CLUSTER_NAME = "demo-cluster";

    private static final String ELASTIC_SEARCH = "elasticsearch";

    public ElasticSearchTestContainerConfig() {
        super(DOCKER_ELASTIC);
        this.addFixedExposedPort(9200, 9200);
        this.addFixedExposedPort(9300, 9300);
        this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
    }
}
