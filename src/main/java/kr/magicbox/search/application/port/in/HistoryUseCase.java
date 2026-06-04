package kr.magicbox.search.application.port.in;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;

import java.util.List;

public interface HistoryUseCase {
    void recordViewedCreator(Long userId, Long creatorId);
    List<CreatorDocument> getViewedCreators(Long userId);
    List<String> getSearchQueries(Long userId);
}
