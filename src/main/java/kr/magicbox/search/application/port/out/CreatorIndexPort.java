package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;

import java.util.List;
import java.util.Optional;

public interface CreatorIndexPort {
    Optional<CreatorDocument> findByCreatorId(Long creatorId);
    void save(CreatorDocument document);
    void update(Long creatorId, String nickname, String tagline, String profileImageUrl);
    void delete(Long creatorId);
    List<CreatorDocument> search(String keyword, int page, int size);
    List<CreatorDocument> findPopular(int size);
    List<CreatorDocument> findRecent(int size);
}
