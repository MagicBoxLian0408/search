package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CreatorRevokedEvent(
        @JsonProperty("creator_id") Long creatorId,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {
}
