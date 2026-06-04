package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;

import java.util.List;
import java.util.Optional;

public interface SearchCachePort {

    // Cache Aside - 인기 목록
    Optional<List<CreatorDocument>> getPopularCreators();
    void setPopularCreators(List<CreatorDocument> creators);

    Optional<List<ReleaseDocument>> getPopularReleases();
    void setPopularReleases(List<ReleaseDocument> releases);

    Optional<List<GeneralGoodsDocument>> getPopularGeneralGoods();
    void setPopularGeneralGoods(List<GeneralGoodsDocument> goods);

    // Write Through - 최신 목록
    void addRecentCreator(CreatorDocument document);
    List<CreatorDocument> getRecentCreators();

    void addRecentRelease(ReleaseDocument document);
    List<ReleaseDocument> getRecentReleases();

    void addRecentGeneralGoods(GeneralGoodsDocument document);
    List<GeneralGoodsDocument> getRecentGeneralGoods();

    // Write Through - 개인화 이력
    void addViewedCreator(Long userId, CreatorDocument document);
    List<CreatorDocument> getViewedCreators(Long userId);

    void addSearchQuery(Long userId, String query);
    List<String> getSearchQueries(Long userId);
}
