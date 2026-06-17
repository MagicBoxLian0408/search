package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record CreatorCertificationApprovedEvent(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("creator_id") Long creatorId,
        @JsonProperty("nickname") String nickname,
        @JsonProperty("profile_image_url") String profileImageUrl,
        @JsonProperty("genres") List<String> genres,
        @JsonProperty("status") String status,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {
}
