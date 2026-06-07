package kr.magicbox.search.application.service;

import kr.magicbox.search.application.dto.query.SearchCreatorsQuery;
import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import kr.magicbox.search.application.port.in.SearchCreatorsUseCase;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchCreatorsService implements SearchCreatorsUseCase {

    private final CreatorIndexPort creatorIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public Mono<List<CreatorSearchResult>> searchCreators(SearchCreatorsQuery query) {
        return searchCachePort.addSearchQuery(query.userId(), query.keyword())
                .then(searchCachePort.incrementQueryScore(query.keyword()))
                .then(creatorIndexPort.searchByKeyword(query.keyword(), query.page(), query.size()))
                .map(docs -> docs.stream().map(doc -> CreatorSearchResult.builder()
                        .creatorId(doc.getCreatorId())
                        .userId(doc.getUserId())
                        .nickname(doc.getNickname())
                        .tagline(doc.getTagline())
                        .profileImageUrl(doc.getProfileImageUrl())
                        .genres(doc.getGenres())
                        .followerCount(doc.getFollowerCount())
                        .build()).toList());
    }
}
