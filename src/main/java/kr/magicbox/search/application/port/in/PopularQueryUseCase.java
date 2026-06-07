package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PopularQueryUseCase {
    Mono<List<CreatorSearchResult>> getPopularCreators();
    Mono<List<ReleaseSearchResult>> getPopularReleases();
    Mono<List<GeneralGoodsSearchResult>> getPopularGeneralGoods();
    Mono<List<String>> getPopularQueries();
}
