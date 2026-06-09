package kr.magicbox.search.adapter.out.elasticsearch.repository;

import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface GeneralGoodsElasticsearchRepository extends ReactiveElasticsearchRepository<GeneralGoodsDocument, String> {
}
