package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.query.SearchGeneralGoodsQuery;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SearchGeneralGoodsUseCase {
    Mono<List<GeneralGoodsSearchResult>> searchGeneralGoods(SearchGeneralGoodsQuery query);
}
