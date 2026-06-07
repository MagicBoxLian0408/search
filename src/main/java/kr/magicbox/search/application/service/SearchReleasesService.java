package kr.magicbox.search.application.service;

import kr.magicbox.search.application.dto.query.SearchReleasesQuery;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import kr.magicbox.search.application.port.in.SearchReleasesUseCase;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchReleasesService implements SearchReleasesUseCase {

    private final ReleaseIndexPort releaseIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public Mono<List<ReleaseSearchResult>> searchReleases(SearchReleasesQuery query) {
        return searchCachePort.addSearchQuery(query.userId(), query.keyword())
                .then(searchCachePort.incrementQueryScore(query.keyword()))
                .then(releaseIndexPort.searchByKeyword(query.keyword(), query.page(), query.size()))
                .map(docs -> docs.stream().map(doc -> ReleaseSearchResult.builder()
                        .releaseId(doc.getReleaseId())
                        .creatorId(doc.getCreatorId())
                        .title(doc.getTitle())
                        .description(doc.getDescription())
                        .level(doc.getLevel())
                        .status(doc.getStatus())
                        .price(doc.getPrice())
                        .limitedQuantity(doc.getLimitedQuantity())
                        .mediaUrls(doc.getMediaUrls())
                        .scheduledAt(doc.getScheduledAt())
                        .likeCount(doc.getLikeCount())
                        .build()).toList());
    }
}
