package kr.magicbox.search.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.repository.CreatorElasticsearchRepository;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CreatorElasticsearchAdapter implements CreatorIndexPort {

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
            CreatorDocument updated = CreatorDocument.builder()
                    .id(doc.getId())
                    .creatorId(doc.getCreatorId())
                    .nickname(nickname != null ? nickname : doc.getNickname())
                    .tagline(tagline != null ? tagline : doc.getTagline())
                    .profileImageUrl(profileImageUrl != null ? profileImageUrl : doc.getProfileImageUrl())
                    .genres(doc.getGenres())
                    .createdAt(doc.getCreatedAt())
                    .build();
            creatorElasticsearchRepository.save(updated);
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
                    .index("creator-index")
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
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] Creator 검색 실패. keyword={}", keyword, e);
            return List.of();
        }
    }

    @Override
    public List<CreatorDocument> findPopular(int size) {
        try {
            SearchResponse<CreatorDocument> response = elasticsearchClient.search(s -> s
                    .index("creator-index")
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
                    CreatorDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] 인기 Creator 조회 실패", e);
            return List.of();
        }
    }

    @Override
    public List<CreatorDocument> findRecent(int size) {
        try {
            SearchResponse<CreatorDocument> response = elasticsearchClient.search(s -> s
                    .index("creator-index")
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field("created_at").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
                    CreatorDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("[ES] 최신 Creator 조회 실패", e);
            return List.of();
        }
    }
}
