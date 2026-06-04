package kr.magicbox.search.application.service;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.application.port.in.HistoryUseCase;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService implements HistoryUseCase {

    private final CreatorIndexPort creatorIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public void recordViewedCreator(Long userId, Long creatorId) {
        creatorIndexPort.findByCreatorId(creatorId).ifPresent(doc ->
                searchCachePort.addViewedCreator(userId, doc)
        );
    }

    @Override
    public List<CreatorDocument> getViewedCreators(Long userId) {
        return searchCachePort.getViewedCreators(userId);
    }

    @Override
    public List<String> getSearchQueries(Long userId) {
        return searchCachePort.getSearchQueries(userId);
    }
}
