package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface HistoryUseCase {
    Mono<Void> recordViewedCreator(Long userId, Long creatorId);
    Mono<List<CreatorSearchResult>> getViewedCreators(Long userId);

    Mono<Void> recordViewedRelease(Long userId, Long releaseId);
    Mono<List<ReleaseSearchResult>> getViewedReleases(Long userId);

    Mono<Void> recordViewedGeneralGoods(Long userId, Long generalGoodsId);
    Mono<List<GeneralGoodsSearchResult>> getViewedGeneralGoods(Long userId);

    Mono<List<String>> getSearchQueries(Long userId);
}
