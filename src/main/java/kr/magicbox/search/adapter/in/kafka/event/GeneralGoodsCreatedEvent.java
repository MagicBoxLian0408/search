package kr.magicbox.search.adapter.in.kafka.event;

import java.time.Instant;
import java.util.List;

public record GeneralGoodsCreatedEvent(
        Long generalGoodsId,
        Long creatorId,
        String name,
        String description,
        String level,
        List<String> categories,
        Long price,
        Long stock,
        List<String> mediaUrls,
        Instant occurredAt
) implements InboxEvent {
}
