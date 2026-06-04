package kr.magicbox.search.application.service;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.port.in.RecentQueryUseCase;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentQueryService implements RecentQueryUseCase {

    private final SearchCachePort searchCachePort;

    @Override
    public List<CreatorDocument> getRecentCreators() {
        return searchCachePort.getRecentCreators();
    }

    @Override
    public List<ReleaseDocument> getRecentReleases() {
        return searchCachePort.getRecentReleases();
    }

    @Override
    public List<GeneralGoodsDocument> getRecentGeneralGoods() {
        return searchCachePort.getRecentGeneralGoods();
    }
}
