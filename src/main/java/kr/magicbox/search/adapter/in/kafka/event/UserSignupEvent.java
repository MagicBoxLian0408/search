package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserSignupEvent(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("signup_at") Instant signupAt
) implements InboxEvent {

    @Override
    public Instant occurredAt() {
        return signupAt;
    }
}
