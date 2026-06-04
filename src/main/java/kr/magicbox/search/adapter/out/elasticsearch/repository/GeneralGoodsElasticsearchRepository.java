package kr.magicbox.search.adapter.out.elasticsearch.repository;

import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface GeneralGoodsElasticsearchRepository extends ElasticsearchRepository<GeneralGoodsDocument, String> {
    Optional<GeneralGoodsDocument> findByGeneralGoodsId(Long generalGoodsId);
    void deleteByGeneralGoodsId(Long generalGoodsId);
}
