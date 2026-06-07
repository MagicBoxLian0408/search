package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RecentQueryUseCase {
    Mono<List<CreatorSearchResult>> getRecentCreators();
    Mono<List<ReleaseSearchResult>> getRecentReleases();
    Mono<List<GeneralGoodsSearchResult>> getRecentGeneralGoods();
}
