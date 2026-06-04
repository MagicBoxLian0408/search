package kr.magicbox.search.application.service;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.port.in.PopularQueryUseCase;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import kr.magicbox.search.adapter.out.cache.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularQueryService implements PopularQueryUseCase {

    private final CreatorIndexPort creatorIndexPort;
    private final ReleaseIndexPort releaseIndexPort;
    private final GeneralGoodsIndexPort generalGoodsIndexPort;
    private final SearchCachePort searchCachePort;
    private final CacheProperties cacheProperties;

    @Override
    public List<CreatorDocument> getPopularCreators() {
        return searchCachePort.getPopularCreators().orElseGet(() -> {
            List<CreatorDocument> result = creatorIndexPort.findPopular(cacheProperties.getPopularSize());
            searchCachePort.setPopularCreators(result);
            return result;
        });
    }

    @Override
    public List<ReleaseDocument> getPopularReleases() {
        return searchCachePort.getPopularReleases().orElseGet(() -> {
            List<ReleaseDocument> result = releaseIndexPort.findPopular(cacheProperties.getPopularSize());
            searchCachePort.setPopularReleases(result);
            return result;
        });
    }

    @Override
    public List<GeneralGoodsDocument> getPopularGeneralGoods() {
        return searchCachePort.getPopularGeneralGoods().orElseGet(() -> {
            List<GeneralGoodsDocument> result = generalGoodsIndexPort.findPopular(cacheProperties.getPopularSize());
            searchCachePort.setPopularGeneralGoods(result);
            return result;
        });
    }
}
