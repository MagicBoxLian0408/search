package kr.magicbox.search.adapter.out.elasticsearch;

import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReleaseElasticsearchAdapter implements ReleaseIndexPort {

    private final ReactiveElasticsearchOperations operations;

    @Override
    public Mono<Void> save(ReleaseDocument document) {
        return operations.save(document).then();
    }

    @Override
    public Mono<Void> updateRelease(Long releaseId, String title, String description, List<String> mediaUrls) {
        return findByReleaseId(releaseId)
                .flatMap(doc -> {
                    ReleaseDocument updated = ReleaseDocument.builder()
                            .id(doc.getId())
                            .releaseId(doc.getReleaseId())
                            .creatorId(doc.getCreatorId())
                            .title(title != null ? title : doc.getTitle())
                            .description(description != null ? description : doc.getDescription())
                            .level(doc.getLevel())
                            .status(doc.getStatus())
                            .price(doc.getPrice())
                            .limitedQuantity(doc.getLimitedQuantity())
                            .mediaUrls(mediaUrls != null ? mediaUrls : doc.getMediaUrls())
                            .scheduledAt(doc.getScheduledAt())
                            .likeCount(doc.getLikeCount())
                            .createdAt(doc.getCreatedAt())
                            .build();
                    return operations.save(updated);
                }).then();
    }

    @Override
    public Mono<Void> deleteByReleaseId(Long releaseId) {
        return findByReleaseId(releaseId)
                .flatMap(doc -> operations.delete(doc.getId(), ReleaseDocument.class))
                .then();
    }

    @Override
    public Mono<ReleaseDocument> findByReleaseId(Long releaseId) {
        CriteriaQuery query = new CriteriaQuery(Criteria.where("releaseId").is(releaseId));
        return operations.search(query, ReleaseDocument.class)
                .next()
                .mapNotNull(SearchHit::getContent);
    }

    @Override
    public Mono<List<ReleaseDocument>> searchByKeyword(String keyword, int page, int size) {
        Criteria criteria = new Criteria("title").matches(keyword)
                .or(new Criteria("description").matches(keyword));
        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(page, size));
        return operations.search(query, ReleaseDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<ReleaseDocument>> findPopular(int size) {
        CriteriaQuery query = new CriteriaQuery(new Criteria())
                .setPageable(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "likeCount")));
        return operations.search(query, ReleaseDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<ReleaseDocument>> findRecent(int size) {
        CriteriaQuery query = new CriteriaQuery(new Criteria())
                .setPageable(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return operations.search(query, ReleaseDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<String>> suggest(String keyword) {
        CriteriaQuery query = new CriteriaQuery(new Criteria("title").startsWith(keyword))
                .setPageable(PageRequest.of(0, 10));
        return operations.search(query, ReleaseDocument.class)
                .map(hit -> hit.getContent().getTitle())
                .collectList();
    }
}
