package kr.magicbox.search.adapter.out.persistence.repository;

import kr.magicbox.search.adapter.out.persistence.entity.SearchInboxEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface SearchInboxRepository extends R2dbcRepository<SearchInboxEntity, Long> {
    Mono<Boolean> existsByEventKey(String eventKey);
    Mono<SearchInboxEntity> findByTopicAndPartitionAndOffset(String topic, Integer partition, Long offset);
}
