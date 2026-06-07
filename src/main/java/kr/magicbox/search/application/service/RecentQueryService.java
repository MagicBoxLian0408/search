package kr.magicbox.search.application.service;

import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import kr.magicbox.search.application.port.in.RecentQueryUseCase;
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
public class RecentQueryService implements RecentQueryUseCase {

    private static final int RECENT_SIZE = 20;

    private final CreatorIndexPort creatorIndexPort;
    private final ReleaseIndexPort releaseIndexPort;
    private final GeneralGoodsIndexPort generalGoodsIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public Mono<List<CreatorSearchResult>> getRecentCreators() {
        return searchCachePort.getRecentCreators()
                .switchIfEmpty(creatorIndexPort.findRecent(RECENT_SIZE)
                        .flatMap(docs -> searchCachePort.setRecentCreators(docs).thenReturn(docs)))
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

    @Override
    public Mono<List<ReleaseSearchResult>> getRecentReleases() {
        return searchCachePort.getRecentReleases()
                .switchIfEmpty(releaseIndexPort.findRecent(RECENT_SIZE)
                        .flatMap(docs -> searchCachePort.setRecentReleases(docs).thenReturn(docs)))
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

    @Override
    public Mono<List<GeneralGoodsSearchResult>> getRecentGeneralGoods() {
        return searchCachePort.getRecentGeneralGoods()
                .switchIfEmpty(generalGoodsIndexPort.findRecent(RECENT_SIZE)
                        .flatMap(docs -> searchCachePort.setRecentGeneralGoods(docs).thenReturn(docs)))
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
    }
}
