package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record CreatorProfileUpdatedEvent(
        @JsonProperty("creator_id") Long creatorId,
        @JsonProperty("after") ProfileSnapshot after,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {

    public record ProfileSnapshot(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("tagline") String tagline,
            @JsonProperty("profile_image_url") String profileImageUrl,
            @JsonProperty("introduction") String introduction,
            @JsonProperty("genres") List<String> genres
    ) {
    }
}
