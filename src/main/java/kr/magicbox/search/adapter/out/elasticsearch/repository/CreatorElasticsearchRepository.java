package kr.magicbox.search.adapter.out.elasticsearch.repository;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface CreatorElasticsearchRepository extends ElasticsearchRepository<CreatorDocument, String> {
    Optional<CreatorDocument> findByCreatorId(Long creatorId);
    void deleteByCreatorId(Long creatorId);
}
