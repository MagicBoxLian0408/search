package kr.magicbox.search.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.repository.CreatorElasticsearchRepository;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CreatorElasticsearchAdapter implements CreatorIndexPort {

    private static final String INDEX = "creator-index";

    private final CreatorElasticsearchRepository creatorElasticsearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Optional<CreatorDocument> findByCreatorId(Long creatorId) {
        return creatorElasticsearchRepository.findByCreatorId(creatorId);
    }

    @Override
    public void save(CreatorDocument document) {
        creatorElasticsearchRepository.save(document);
    }

    @Override
    public void update(Long creatorId, String nickname, String tagline, String profileImageUrl) {
        creatorElasticsearchRepository.findByCreatorId(creatorId).ifPresent(doc -> {
            creatorElasticsearchRepository.save(CreatorDocument.builder()
                    .id(doc.getId())
                    .creatorId(doc.getCreatorId())
                    .nickname(nickname != null ? nickname : doc.getNickname())
                    .tagline(tagline != null ? tagline : doc.getTagline())
                    .profileImageUrl(profileImageUrl != null ? profileImageUrl : doc.getProfileImageUrl())
                    .genres(doc.getGenres())
                    .createdAt(doc.getCreatedAt())
                    .build());
        });
    }

    @Override
    public void delete(Long creatorId) {
        creatorElasticsearchRepository.deleteByCreatorId(creatorId);
    }

    @Override
    public List<CreatorDocument> search(String keyword, int page, int size) {
        try {
            SearchResponse<CreatorDocument> response = elasticsearchClient.search(s -> s
                    .index(INDEX)
                    .from(page * size)
                    .size(size)
                    .query(q -> q
                            .multiMatch(m -> m
                                    .query(keyword)
                                    .fields("nickname", "tagline")
                                    .analyzer("nori")
                            )
                    ),
                    CreatorDocument.class
            );
            return toDocuments(response);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<CreatorDocument> findPopular(int size) {
        return findSortedByCreatedAt(size);
    }

    @Override
    public List<CreatorDocument> findRecent(int size) {
        return findSortedByCreatedAt(size);
    }

    private List<CreatorDocument> findSortedByCreatedAt(int size) {
        try {
            SearchResponse<CreatorDocument> response = elasticsearchClient.search(s -> s
                    .index(INDEX)
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(SortOrder.Desc))),
                    CreatorDocument.class
            );
            return toDocuments(response);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<CreatorDocument> toDocuments(SearchResponse<CreatorDocument> response) {
        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}
