package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.query.SearchCreatorsQuery;
import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SearchCreatorsUseCase {
    Mono<List<CreatorSearchResult>> searchCreators(SearchCreatorsQuery query);
}
