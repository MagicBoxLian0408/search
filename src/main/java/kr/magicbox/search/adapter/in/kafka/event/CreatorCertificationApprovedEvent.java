package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CreatorCertificationApprovedEvent(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("certification_id") Long certificationId,
        @JsonProperty("reviewed_at") Instant reviewedAt
) implements InboxEvent {

    @Override
    public Instant occurredAt() {
        return reviewedAt;
    }
}
