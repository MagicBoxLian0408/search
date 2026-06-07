package kr.magicbox.search.application.dto.command;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record IndexReleaseCommand(
        Long releaseId,
        Long creatorId,
        String title,
        String description,
        String level,
        String status,
        Long price,
        Integer limitedQuantity,
        Instant scheduledAt,
        List<String> mediaUrls
) {
}
