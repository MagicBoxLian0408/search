package kr.magicbox.search.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.adapter.out.elasticsearch.repository.ReleaseElasticsearchRepository;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReleaseElasticsearchAdapter implements ReleaseIndexPort {

    private final ReleaseElasticsearchRepository releaseElasticsearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void save(ReleaseDocument document) {
        releaseElasticsearchRepository.save(document);
    }

    @Override
    public void update(Long releaseId, String title, String description, List<String> mediaUrls) {
        releaseElasticsearchRepository.findByReleaseId(releaseId).ifPresent(doc -> {
            ReleaseDocument updated = ReleaseDocument.builder()
                    .id(doc.getId())
                    .releaseId(doc.getReleaseId())
                    .creatorId(doc.getCreatorId())
                    .title(title != null ? title : doc.getTitle())
                    .description(description != null ? description : doc.getDescription())
                    .level(doc.getLevel())
                    .price(doc.getPrice())
                    .limitedQuantity(doc.getLimitedQuantity())
                    .mediaUrls(mediaUrls != null ? mediaUrls : doc.getMediaUrls())
                    .scheduledAt(doc.getScheduledAt())
                    .createdAt(doc.getCreatedAt())
                    .build();
            releaseElasticsearchRepository.save(updated);
        });
    }

    @Override
    public void delete(Long releaseId) {
        releaseElasticsearchRepository.deleteByReleaseId(releaseId);
    }

    @Override
    public List<ReleaseDocument> search(String keyword, int page, int size) {
        try {
            SearchResponse<ReleaseDocument> response = elasticsearchClient.search(s -> s
                    .index("release-index")
                    .from(page * size)
                    .size(size)
                    .query(q -> q
                            .multiMatch(m -> m
                                    .query(keyword)
                                    .fields("title", "description")
                                    .analyzer("nori")
                            )
                    ),
                    ReleaseDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] Release 검색 실패. keyword={}", keyword, e);
            return List.of();
        }
    }

    @Override
    public List<ReleaseDocument> findPopular(int size) {
        try {
            SearchResponse<ReleaseDocument> response = elasticsearchClient.search(s -> s
                    .index("release-index")
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
                    ReleaseDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] 인기 Release 조회 실패", e);
            return List.of();
        }
    }

    @Override
    public List<ReleaseDocument> findRecent(int size) {
        try {
            SearchResponse<ReleaseDocument> response = elasticsearchClient.search(s -> s
                    .index("release-index")
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
                    ReleaseDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] 최신 Release 조회 실패", e);
            return List.of();
        }
    }
}
