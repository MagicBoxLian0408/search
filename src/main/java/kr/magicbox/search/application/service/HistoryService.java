package kr.magicbox.search.application.service;

import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import kr.magicbox.search.application.port.in.HistoryUseCase;
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
public class HistoryService implements HistoryUseCase {

    private final CreatorIndexPort creatorIndexPort;
    private final ReleaseIndexPort releaseIndexPort;
    private final GeneralGoodsIndexPort generalGoodsIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public Mono<Void> recordViewedCreator(Long userId, Long creatorId) {
        return creatorIndexPort.findByCreatorId(creatorId)
                .flatMap(doc -> searchCachePort.addViewedCreator(userId, doc));
    }

    @Override
    public Mono<List<CreatorSearchResult>> getViewedCreators(Long userId) {
        return searchCachePort.getViewedCreators(userId)
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
    public Mono<Void> recordViewedRelease(Long userId, Long releaseId) {
        return releaseIndexPort.findByReleaseId(releaseId)
                .flatMap(doc -> searchCachePort.addViewedRelease(userId, doc));
    }

    @Override
    public Mono<List<ReleaseSearchResult>> getViewedReleases(Long userId) {
        return searchCachePort.getViewedReleases(userId)
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
    public Mono<Void> recordViewedGeneralGoods(Long userId, Long generalGoodsId) {
        return generalGoodsIndexPort.findByGeneralGoodsId(generalGoodsId)
                .flatMap(doc -> searchCachePort.addViewedGeneralGoods(userId, doc));
    }

    @Override
    public Mono<List<GeneralGoodsSearchResult>> getViewedGeneralGoods(Long userId) {
        return searchCachePort.getViewedGeneralGoods(userId)
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

    @Override
    public Mono<List<String>> getSearchQueries(Long userId) {
        return searchCachePort.getSearchQueries(userId);
    }
}
