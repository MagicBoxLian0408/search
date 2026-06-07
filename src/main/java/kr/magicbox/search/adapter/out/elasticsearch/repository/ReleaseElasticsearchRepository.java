package kr.magicbox.search.adapter.out.elasticsearch.repository;

import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface ReleaseElasticsearchRepository extends ElasticsearchRepository<ReleaseDocument, String> {
    Optional<ReleaseDocument> findByReleaseId(Long releaseId);
    void deleteByReleaseId(Long releaseId);
}
