package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.query.SearchCreatorsQuery;
import kr.magicbox.search.application.dto.result.SearchAllResult;
import reactor.core.publisher.Mono;

public interface SearchAllUseCase {
    Mono<SearchAllResult> searchAll(SearchCreatorsQuery query);
}
