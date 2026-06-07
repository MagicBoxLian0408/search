package kr.magicbox.search.application.service;

import kr.magicbox.search.application.dto.query.SearchCreatorsQuery;
import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import kr.magicbox.search.application.dto.result.SearchAllResult;
import kr.magicbox.search.application.port.in.SearchAllUseCase;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchAllService implements SearchAllUseCase {

    private final CreatorIndexPort creatorIndexPort;
    private final ReleaseIndexPort releaseIndexPort;
    private final GeneralGoodsIndexPort generalGoodsIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public Mono<SearchAllResult> searchAll(SearchCreatorsQuery query) {
        Mono<Void> recordHistory = searchCachePort.addSearchQuery(query.userId(), query.keyword())
                .then(searchCachePort.incrementQueryScore(query.keyword()));

        Mono<List<CreatorSearchResult>> creators = creatorIndexPort.searchByKeyword(query.keyword(), query.page(), query.size())
                .map(docs -> docs.stream().map(doc -> CreatorSearchResult.builder()
                        .creatorId(doc.getCreatorId())
                        .userId(doc.getUserId())
                        .nickname(doc.getNickname())
                        .tagline(doc.getTagline())
                        .profileImageUrl(doc.getProfileImageUrl())
                        .genres(doc.getGenres())
                        .followerCount(doc.getFollowerCount())
                        .build()).toList());

        Mono<List<ReleaseSearchResult>> releases = releaseIndexPort.searchByKeyword(query.keyword(), query.page(), query.size())
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

        Mono<List<GeneralGoodsSearchResult>> generalGoods = generalGoodsIndexPort.searchByKeyword(query.keyword(), query.page(), query.size())
                .map(docs -> docs.stream().map(doc -> GeneralGoodsSearchResult.builder()
                        .generalGoodsId(doc.getGeneralGoodsId())
                        .creatorId(doc.getCreatorId())
                        .name(doc.getName())
                        .description(doc.getDescription())
                        .level(doc.getLevel())
                        .categories(doc.getCategories())
                        .price(doc.getPrice())
                        .stock(doc.getStock())
                        .mediaUrls(doc.getMediaUrls())
                        .likeCount(doc.getLikeCount())
                        .build()).toList());

        return recordHistory.then(
                Mono.zip(creators, releases, generalGoods)
                        .map(tuple -> new SearchAllResult(tuple.getT1(), tuple.getT2(), tuple.getT3()))
        );
    }
}
