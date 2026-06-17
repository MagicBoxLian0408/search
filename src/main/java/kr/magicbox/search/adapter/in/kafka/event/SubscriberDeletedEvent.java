package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record SubscriberDeletedEvent(
        @JsonProperty("creator_id") Long creatorId,
        @JsonProperty("subscriber_id") Long subscriberId,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {}
