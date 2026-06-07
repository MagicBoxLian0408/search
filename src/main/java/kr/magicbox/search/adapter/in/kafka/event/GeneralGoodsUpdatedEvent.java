package kr.magicbox.search.adapter.in.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GeneralGoodsUpdatedEvent(
        @JsonProperty("general_goods_id") Long generalGoodsId,
        @JsonProperty("after") GoodsSnapshot after,
        @JsonProperty("occurred_at") Instant occurredAt
) implements InboxEvent {

    public record GoodsSnapshot(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("level") String level,
            @JsonProperty("categories") List<String> categories,
            @JsonProperty("price") Long price,
            @JsonProperty("stock") Long stock,
            @JsonProperty("media_urls") List<String> mediaUrls
    ) {
    }
}
