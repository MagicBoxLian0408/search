package kr.magicbox.search.adapter.out.elasticsearch;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreatorElasticsearchAdapter implements CreatorIndexPort {

    private final ReactiveElasticsearchOperations operations;

    @Override
    public Mono<Void> save(CreatorDocument document) {
        return operations.save(document).then();
    }

    @Override
    public Mono<Void> updateProfile(Long creatorId, String nickname, String tagline, String profileImageUrl, String introduction, List<String> genres) {
        return findByCreatorId(creatorId)
                .flatMap(doc -> {
                    CreatorDocument updated = CreatorDocument.builder()
                            .id(doc.getId())
                            .creatorId(doc.getCreatorId())
                            .userId(doc.getUserId())
                            .nickname(nickname != null ? nickname : doc.getNickname())
                            .tagline(tagline != null ? tagline : doc.getTagline())
                            .profileImageUrl(profileImageUrl != null ? profileImageUrl : doc.getProfileImageUrl())
                            .introduction(introduction != null ? introduction : doc.getIntroduction())
                            .genres(genres != null ? genres : doc.getGenres())
                            .followerCount(doc.getFollowerCount())
                            .status(doc.getStatus())
                            .createdAt(doc.getCreatedAt())
                            .build();
                    return operations.save(updated);
                }).then();
    }

    @Override
    public Mono<Void> updateStatus(Long creatorId, String status) {
        return findByCreatorId(creatorId)
                .flatMap(doc -> {
                    CreatorDocument updated = CreatorDocument.builder()
                            .id(doc.getId())
                            .creatorId(doc.getCreatorId())
                            .userId(doc.getUserId())
                            .nickname(doc.getNickname())
                            .tagline(doc.getTagline())
                            .profileImageUrl(doc.getProfileImageUrl())
                            .introduction(doc.getIntroduction())
                            .genres(doc.getGenres())
                            .followerCount(doc.getFollowerCount())
                            .status(status)
                            .createdAt(doc.getCreatedAt())
                            .build();
                    return operations.save(updated);
                }).then();
    }

    @Override
    public Mono<CreatorDocument> findByCreatorId(Long creatorId) {
        CriteriaQuery query = new CriteriaQuery(Criteria.where("creatorId").is(creatorId));
        return operations.search(query, CreatorDocument.class)
                .next()
                .mapNotNull(SearchHit::getContent);
    }

    @Override
    public Mono<List<CreatorDocument>> searchByKeyword(String keyword, int page, int size) {
        Criteria criteria = new Criteria("status").is("ACTIVE")
                .and(new Criteria("nickname").matches(keyword)
                        .or(new Criteria("tagline").matches(keyword))
                        .or(new Criteria("introduction").matches(keyword)));
        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(page, size));
        return operations.search(query, CreatorDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<CreatorDocument>> findPopular(int size) {
        CriteriaQuery query = new CriteriaQuery(Criteria.where("status").is("ACTIVE"))
                .setPageable(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "followerCount")));
        return operations.search(query, CreatorDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<CreatorDocument>> findRecent(int size) {
        CriteriaQuery query = new CriteriaQuery(Criteria.where("status").is("ACTIVE"))
                .setPageable(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return operations.search(query, CreatorDocument.class)
                .map(SearchHit::getContent)
                .collectList();
    }

    @Override
    public Mono<List<String>> suggest(String keyword) {
        CriteriaQuery query = new CriteriaQuery(
                new Criteria("status").is("ACTIVE")
                        .and(new Criteria("nickname").startsWith(keyword)))
                .setPageable(PageRequest.of(0, 10));
        return operations.search(query, CreatorDocument.class)
                .map(hit -> hit.getContent().getNickname())
                .collectList();
    }
}
