package kr.magicbox.search.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.repository.GeneralGoodsElasticsearchRepository;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GeneralGoodsElasticsearchAdapter implements GeneralGoodsIndexPort {

    private final GeneralGoodsElasticsearchRepository generalGoodsElasticsearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void save(GeneralGoodsDocument document) {
        generalGoodsElasticsearchRepository.save(document);
    }

    @Override
    public void update(Long generalGoodsId, String name, Long price, Long stock, List<String> mediaUrls) {
        generalGoodsElasticsearchRepository.findByGeneralGoodsId(generalGoodsId).ifPresent(doc -> {
            GeneralGoodsDocument updated = GeneralGoodsDocument.builder()
                    .id(doc.getId())
                    .generalGoodsId(doc.getGeneralGoodsId())
                    .creatorId(doc.getCreatorId())
                    .name(name != null ? name : doc.getName())
                    .price(price != null ? price : doc.getPrice())
                    .stock(stock != null ? stock : doc.getStock())
                    .mediaUrls(mediaUrls != null ? mediaUrls : doc.getMediaUrls())
                    .createdAt(doc.getCreatedAt())
                    .build();
            generalGoodsElasticsearchRepository.save(updated);
        });
    }

    @Override
    public void delete(Long generalGoodsId) {
        generalGoodsElasticsearchRepository.deleteByGeneralGoodsId(generalGoodsId);
    }

    @Override
    public List<GeneralGoodsDocument> search(String keyword, int page, int size) {
        try {
            SearchResponse<GeneralGoodsDocument> response = elasticsearchClient.search(s -> s
                    .index("general-goods-index")
                    .from(page * size)
                    .size(size)
                    .query(q -> q
                            .multiMatch(m -> m
                                    .query(keyword)
                                    .fields("name")
                                    .analyzer("nori")
                            )
                    ),
                    GeneralGoodsDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] GeneralGoods 검색 실패. keyword={}", keyword, e);
            return List.of();
        }
    }

    @Override
    public List<GeneralGoodsDocument> findPopular(int size) {
        try {
            SearchResponse<GeneralGoodsDocument> response = elasticsearchClient.search(s -> s
                    .index("general-goods-index")
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
                    GeneralGoodsDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] 인기 GeneralGoods 조회 실패", e);
            return List.of();
        }
    }

    @Override
    public List<GeneralGoodsDocument> findRecent(int size) {
        try {
            SearchResponse<GeneralGoodsDocument> response = elasticsearchClient.search(s -> s
                    .index("general-goods-index")
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
                    GeneralGoodsDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] 최신 GeneralGoods 조회 실패", e);
            return List.of();
        }
    }
}
