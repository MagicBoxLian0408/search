package kr.magicbox.search.adapter.in.kafka.event;

import java.time.Instant;

public record GeneralGoodsDeletedEvent(
        Long generalGoodsId,
        Instant occurredAt
) implements InboxEvent {
}
