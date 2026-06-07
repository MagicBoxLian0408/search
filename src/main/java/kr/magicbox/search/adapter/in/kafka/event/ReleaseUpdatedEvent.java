package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record ReleaseUpdatedEvent(
        @JsonProperty("release_id") Long releaseId,
        @JsonProperty("after") ReleaseSnapshot after,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {

    public record ReleaseSnapshot(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("media_urls") List<String> mediaUrls
    ) {
    }
}
