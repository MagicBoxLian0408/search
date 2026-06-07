package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.query.SearchReleasesQuery;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SearchReleasesUseCase {
    Mono<List<ReleaseSearchResult>> searchReleases(SearchReleasesQuery query);
}
