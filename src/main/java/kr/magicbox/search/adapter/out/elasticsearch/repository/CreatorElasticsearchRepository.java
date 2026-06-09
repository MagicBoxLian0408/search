package kr.magicbox.search.adapter.out.elasticsearch.repository;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface CreatorElasticsearchRepository extends ReactiveElasticsearchRepository<CreatorDocument, String> {
}
