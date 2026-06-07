package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReleaseIndexPort {
    Mono<Void> save(ReleaseDocument document);
    Mono<Void> updateRelease(Long releaseId, String title, String description, List<String> mediaUrls);
    Mono<Void> deleteByReleaseId(Long releaseId);
    Mono<ReleaseDocument> findByReleaseId(Long releaseId);
    Mono<List<ReleaseDocument>> searchByKeyword(String keyword, int page, int size);
    Mono<List<ReleaseDocument>> findPopular(int size);
    Mono<List<ReleaseDocument>> findRecent(int size);
    Mono<List<String>> suggest(String keyword);
}
