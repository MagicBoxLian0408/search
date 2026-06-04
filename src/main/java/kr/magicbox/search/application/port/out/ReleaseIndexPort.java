package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;

import java.util.List;

public interface ReleaseIndexPort {
    void save(ReleaseDocument document);
    void update(Long releaseId, String title, String description, List<String> mediaUrls);
    void delete(Long releaseId);
    List<ReleaseDocument> search(String keyword, int page, int size);
    List<ReleaseDocument> findPopular(int size);
    List<ReleaseDocument> findRecent(int size);
}
