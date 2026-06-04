package kr.magicbox.search.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.adapter.out.elasticsearch.repository.ReleaseElasticsearchRepository;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReleaseElasticsearchAdapter implements ReleaseIndexPort {

    private static final String INDEX = "release-index";

    private final ReleaseElasticsearchRepository releaseElasticsearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void save(ReleaseDocument document) {
        releaseElasticsearchRepository.save(document);
    }

    @Override
    public void update(Long releaseId, String title, String description, List<String> mediaUrls) {
        releaseElasticsearchRepository.findByReleaseId(releaseId).ifPresent(doc -> {
            releaseElasticsearchRepository.save(ReleaseDocument.builder()
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
                    .build());
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
                    .index(INDEX)
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
            return toDocuments(response);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<ReleaseDocument> findPopular(int size) {
        return findSortedByCreatedAt(size);
    }

    @Override
    public List<ReleaseDocument> findRecent(int size) {
        return findSortedByCreatedAt(size);
    }

    private List<ReleaseDocument> findSortedByCreatedAt(int size) {
        try {
            SearchResponse<ReleaseDocument> response = elasticsearchClient.search(s -> s
                    .index(INDEX)
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(SortOrder.Desc))),
                    ReleaseDocument.class
            );
            return toDocuments(response);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<ReleaseDocument> toDocuments(SearchResponse<ReleaseDocument> response) {
        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}
