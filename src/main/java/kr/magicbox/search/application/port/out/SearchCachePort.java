package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SearchCachePort {

    // 인기 목록 캐시 (Cache Aside)
    Mono<List<CreatorDocument>> getPopularCreators();
    Mono<Void> setPopularCreators(List<CreatorDocument> list);

    Mono<List<ReleaseDocument>> getPopularReleases();
    Mono<Void> setPopularReleases(List<ReleaseDocument> list);

    Mono<List<GeneralGoodsDocument>> getPopularGeneralGoods();
    Mono<Void> setPopularGeneralGoods(List<GeneralGoodsDocument> list);

    // 최신 목록 캐시
    Mono<List<CreatorDocument>> getRecentCreators();
    Mono<Void> setRecentCreators(List<CreatorDocument> list);

    Mono<List<ReleaseDocument>> getRecentReleases();
    Mono<Void> setRecentReleases(List<ReleaseDocument> list);

    Mono<List<GeneralGoodsDocument>> getRecentGeneralGoods();
    Mono<Void> setRecentGeneralGoods(List<GeneralGoodsDocument> list);

    // 인기 검색어 (ZSet)
    Mono<Void> incrementQueryScore(String keyword);
    Mono<List<String>> getPopularQueries(int size);

    // 최근 본 아이템
    Mono<Void> addViewedCreator(Long userId, CreatorDocument doc);
    Mono<List<CreatorDocument>> getViewedCreators(Long userId);

    Mono<Void> addViewedRelease(Long userId, ReleaseDocument doc);
    Mono<List<ReleaseDocument>> getViewedReleases(Long userId);

    Mono<Void> addViewedGeneralGoods(Long userId, GeneralGoodsDocument doc);
    Mono<List<GeneralGoodsDocument>> getViewedGeneralGoods(Long userId);

    // 최근 검색어
    Mono<Void> addSearchQuery(Long userId, String keyword);
    Mono<List<String>> getSearchQueries(Long userId);
}
