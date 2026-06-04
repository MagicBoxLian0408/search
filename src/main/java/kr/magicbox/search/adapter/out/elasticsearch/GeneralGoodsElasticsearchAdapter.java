package kr.magicbox.search.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.repository.GeneralGoodsElasticsearchRepository;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GeneralGoodsElasticsearchAdapter implements GeneralGoodsIndexPort {

    private static final String INDEX = "general-goods-index";

    private final GeneralGoodsElasticsearchRepository generalGoodsElasticsearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void save(GeneralGoodsDocument document) {
        generalGoodsElasticsearchRepository.save(document);
    }

    @Override
    public void update(Long generalGoodsId, String name, Long price, Long stock, List<String> mediaUrls) {
        generalGoodsElasticsearchRepository.findByGeneralGoodsId(generalGoodsId).ifPresent(doc -> {
            generalGoodsElasticsearchRepository.save(GeneralGoodsDocument.builder()
                    .id(doc.getId())
                    .generalGoodsId(doc.getGeneralGoodsId())
                    .creatorId(doc.getCreatorId())
                    .name(name != null ? name : doc.getName())
                    .price(price != null ? price : doc.getPrice())
                    .stock(stock != null ? stock : doc.getStock())
                    .mediaUrls(mediaUrls != null ? mediaUrls : doc.getMediaUrls())
                    .createdAt(doc.getCreatedAt())
                    .build());
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
                    .index(INDEX)
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
            return toDocuments(response);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<GeneralGoodsDocument> findPopular(int size) {
        return findSortedByCreatedAt(size);
    }

    @Override
    public List<GeneralGoodsDocument> findRecent(int size) {
        return findSortedByCreatedAt(size);
    }

    private List<GeneralGoodsDocument> findSortedByCreatedAt(int size) {
        try {
            SearchResponse<GeneralGoodsDocument> response = elasticsearchClient.search(s -> s
                    .index(INDEX)
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(SortOrder.Desc))),
                    GeneralGoodsDocument.class
            );
            return toDocuments(response);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<GeneralGoodsDocument> toDocuments(SearchResponse<GeneralGoodsDocument> response) {
        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}
