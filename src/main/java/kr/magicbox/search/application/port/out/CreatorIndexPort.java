package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CreatorIndexPort {
    Mono<Void> save(CreatorDocument document);
    Mono<Void> updateProfile(Long creatorId, String nickname, String tagline, String profileImageUrl, String introduction, List<String> genres);
    Mono<Void> updateStatus(Long creatorId, String status);
    Mono<Void> incrementFollowerCount(Long creatorId);
    Mono<Void> decrementFollowerCount(Long creatorId);
    Mono<CreatorDocument> findByCreatorId(Long creatorId);
    Mono<List<CreatorDocument>> searchByKeyword(String keyword, int page, int size);
    Mono<List<CreatorDocument>> findPopular(int size);
    Mono<List<CreatorDocument>> findRecent(int size);
    Mono<List<String>> suggest(String keyword);
}
