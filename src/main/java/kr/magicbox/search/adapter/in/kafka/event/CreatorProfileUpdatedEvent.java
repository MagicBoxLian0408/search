package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CreatorProfileUpdatedEvent(
        @JsonProperty("creator_id") Long creatorId,
        @JsonProperty("before") ProfileSnapshot before,
        @JsonProperty("after") ProfileSnapshot after,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {

    public record ProfileSnapshot(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("tagline") String tagline,
            @JsonProperty("profile_image_url") String profileImageUrl
    ) {}
}
