package kr.magicbox.search.adapter.in.web.dto.response;

import kr.magicbox.search.application.dto.result.ReleaseSearchResult;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ReleaseSearchResponse(
        Long releaseId,
        Long creatorId,
        String title,
        String description,
        String level,
        String status,
        Long price,
        Integer limitedQuantity,
        List<String> mediaUrls,
        Instant scheduledAt,
        Long likeCount
) {
    public static ReleaseSearchResponse from(ReleaseSearchResult result) {
        return ReleaseSearchResponse.builder()
                .releaseId(result.releaseId())
                .creatorId(result.creatorId())
                .title(result.title())
                .description(result.description())
                .level(result.level())
                .status(result.status())
                .price(result.price())
                .limitedQuantity(result.limitedQuantity())
                .mediaUrls(result.mediaUrls())
                .scheduledAt(result.scheduledAt())
                .likeCount(result.likeCount())
                .build();
    }
}
