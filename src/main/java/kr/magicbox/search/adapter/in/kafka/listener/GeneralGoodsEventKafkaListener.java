package kr.magicbox.search.adapter.in.kafka.listener;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.GeneralGoodsCreatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.GeneralGoodsDeletedEvent;
import kr.magicbox.search.adapter.in.kafka.event.GeneralGoodsUpdatedEvent;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeneralGoodsEventKafkaListener {

    private final GeneralGoodsIndexPort generalGoodsIndexPort;
    private final SearchCachePort searchCachePort;

    @Idempotent
    @KafkaListener(topics = "outbox.event.general-goods-created", groupId = "${spring.kafka.consumer.group-id}")
    public void onCreated(ConsumerRecord<String, GeneralGoodsCreatedEvent> consumerRecord) {
        GeneralGoodsCreatedEvent event = consumerRecord.value();
        GeneralGoodsDocument document = GeneralGoodsDocument.builder()
                .generalGoodsId(event.generalGoodsId())
                .creatorId(event.creatorId())
                .name(event.name())
                .price(event.price())
                .stock(event.stock())
                .mediaUrls(event.mediaUrls())
                .createdAt(event.occurredAt())
                .build();
        generalGoodsIndexPort.save(document);
        searchCachePort.addRecentGeneralGoods(document);
        log.info("[Kafka] GeneralGoods 색인 완료. id={}", event.generalGoodsId());
    }

    @Idempotent
    @KafkaListener(topics = "outbox.event.general-goods-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void onUpdated(ConsumerRecord<String, GeneralGoodsUpdatedEvent> consumerRecord) {
        GeneralGoodsUpdatedEvent event = consumerRecord.value();
        GeneralGoodsUpdatedEvent.GoodsSnapshot after = event.after();
        generalGoodsIndexPort.update(event.generalGoodsId(), after.name(), after.price(), after.stock(), after.mediaUrls());
        log.info("[Kafka] GeneralGoods 업데이트 완료. id={}", event.generalGoodsId());
    }

    @Idempotent
    @KafkaListener(topics = "outbox.event.general-goods-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onDeleted(ConsumerRecord<String, GeneralGoodsDeletedEvent> consumerRecord) {
        GeneralGoodsDeletedEvent event = consumerRecord.value();
        generalGoodsIndexPort.delete(event.generalGoodsId());
        log.info("[Kafka] GeneralGoods 삭제 완료. id={}", event.generalGoodsId());
    }
}
