package kr.magicbox.search.adapter.out.elasticsearch;

import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import lombok.RequiredArgsConstructor;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GeneralGoodsElasticsearchAdapter implements GeneralGoodsIndexPort {

    private final ReactiveElasticsearchOperations operations;

    @Override
    public Mono<Void> save(GeneralGoodsDocument document) {
        return operations.save(document).then();
    }

    @Override
    public Mono<Void> updateGeneralGoods(Long generalGoodsId, String name, String description, String level,
                                         List<String> categories, Long price, Long stock, List<String> mediaUrls) {
        return findByGeneralGoodsId(generalGoodsId)
                .flatMap(doc -> {
                    GeneralGoodsDocument updated = GeneralGoodsDocument.builder()
                            .id(doc.getId())
                            .generalGoodsId(doc.getGeneralGoodsId())
                            .creatorId(doc.getCreatorId())
                            .name(name != null ? name : doc.getName())
                            .description(description != null ? description : doc.getDescription())
                            .level(level != null ? level : doc.getLevel())
                            .categories(categories != null ? categories : doc.getCategories())
                            .price(price != null ? price : doc.getPrice())
                            .stock(stock != null ? stock : doc.getStock())
                            .mediaUrls(mediaUrls != null ? mediaUrls : doc.getMediaUrls())
                            .likeCount(doc.getLikeCount())
                            .isDeleted(doc.getIsDeleted())
                            .createdAt(doc.getCreatedAt())
                            .build();
                    return operations.save(updated);
                }).then();
    }

    @Override
    public Mono<Void> softDelete(Long generalGoodsId) {
        return findByGeneralGoodsId(generalGoodsId)
                .flatMap(doc -> {
                    GeneralGoodsDocument updated = GeneralGoodsDocument.builder()
                            .id(doc.getId())
                            .generalGoodsId(doc.getGeneralGoodsId())
                            .creatorId(doc.getCreatorId())
                            .name(doc.getName())
                            .description(doc.getDescription())
                            .level(doc.getLevel())
                            .categories(doc.getCategories())
                            .price(doc.getPrice())
                            .stock(doc.getStock())
                            .mediaUrls(doc.getMediaUrls())
                            .likeCount(doc.getLikeCount())
                            .isDeleted(true)
                            .createdAt(doc.getCreatedAt())
                            .build();
                    return operations.save(updated);
                }).then();
    }

    @Override
    public Mono<GeneralGoodsDocument> findByGeneralGoodsId(Long generalGoodsId) {
        CriteriaQuery query = new CriteriaQuery(Criteria.where("generalGoodsId").is(generalGoodsId));
        return operations.search(query, GeneralGoodsDocument.class)
                .next()
                .mapNotNull(SearchHit::getContent);
    }

    @Override
    public Mono<List<GeneralGoodsDocument>> searchByKeyword(String keyword, int page, int size) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.term(t -> t.field("isDeleted").value(false)))
                        .must(m -> m.multiMatch(mm -> mm
                                .query(keyword)
                                .fields("name^2", "description")
                                .operator(Operator.And)
                        ))
                ))
                .withPageable(PageRequest.of(page, size))
                .build();
        return operations.search(query, GeneralGoodsDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<GeneralGoodsDocument>> findPopular(int size) {
        CriteriaQuery query = new CriteriaQuery(Criteria.where("isDeleted").is(false))
                .setPageable(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "likeCount")));
        return operations.search(query, GeneralGoodsDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<GeneralGoodsDocument>> findRecent(int size) {
        CriteriaQuery query = new CriteriaQuery(Criteria.where("isDeleted").is(false))
                .setPageable(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return operations.search(query, GeneralGoodsDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<String>> suggest(String keyword) {
        CriteriaQuery query = new CriteriaQuery(
                new Criteria("isDeleted").is(false)
                        .and(new Criteria("name").startsWith(keyword)))
                .setPageable(PageRequest.of(0, 10));
        return operations.search(query, GeneralGoodsDocument.class)
                .map(hit -> hit.getContent().getName())
                .collectList();
    }
}
