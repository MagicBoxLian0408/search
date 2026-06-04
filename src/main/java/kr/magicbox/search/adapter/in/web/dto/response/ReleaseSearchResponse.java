package kr.magicbox.search.adapter.in.web.dto.response;

import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
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
        Long price,
        Integer limitedQuantity,
        List<String> mediaUrls,
        Instant scheduledAt,
        Instant createdAt
) {
    public static ReleaseSearchResponse from(ReleaseDocument doc) {
        return ReleaseSearchResponse.builder()
                .releaseId(doc.getReleaseId())
                .creatorId(doc.getCreatorId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .level(doc.getLevel())
                .price(doc.getPrice())
                .limitedQuantity(doc.getLimitedQuantity())
                .mediaUrls(doc.getMediaUrls())
                .scheduledAt(doc.getScheduledAt())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
