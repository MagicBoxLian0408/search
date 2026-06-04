package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CreatorRevokedEvent(
        @JsonProperty("creator_id") Long creatorId,
        @JsonProperty("revoked_at") Instant revokedAt
) implements InboxEvent {

    @Override
    public Instant occurredAt() {
        return revokedAt;
    }
}
