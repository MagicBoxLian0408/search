package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ReleaseCreatedEvent(
        @JsonProperty("release_id") Long releaseId,
        @JsonProperty("creator_id") Long creatorId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("level") String level,
        @JsonProperty("price") Long price,
        @JsonProperty("limited_quantity") Integer limitedQuantity,
        @JsonProperty("scheduled_at") Instant scheduledAt,
        @JsonProperty("media_urls") List<String> mediaUrls,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {
}
