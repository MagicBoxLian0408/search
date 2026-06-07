package kr.magicbox.search.application.dto.result;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ReleaseSearchResult(
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
}
