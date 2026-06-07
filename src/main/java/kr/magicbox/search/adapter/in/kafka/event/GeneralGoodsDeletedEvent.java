package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record GeneralGoodsDeletedEvent(
        @JsonProperty("general_goods_id") Long generalGoodsId,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {
}
