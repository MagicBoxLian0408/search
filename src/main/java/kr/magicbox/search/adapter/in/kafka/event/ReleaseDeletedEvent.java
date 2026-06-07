package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record ReleaseDeletedEvent(
        @JsonProperty("release_id") Long releaseId,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {
}
