package kr.magicbox.search.adapter.in.kafka.listener;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseCreatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseDeletedEvent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseUpdatedEvent;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseEventKafkaListener {

    private final ReleaseIndexPort releaseIndexPort;
    private final SearchCachePort searchCachePort;

    @Idempotent
    @KafkaListener(topics = "outbox.event.release-created", groupId = "${spring.kafka.consumer.group-id}")
    public void onCreated(ConsumerRecord<String, ReleaseCreatedEvent> consumerRecord) {
        ReleaseCreatedEvent event = consumerRecord.value();
        ReleaseDocument document = ReleaseDocument.builder()
                .releaseId(event.releaseId())
                .creatorId(event.creatorId())
                .title(event.title())
                .description(event.description())
                .level(event.level())
                .price(event.price())
                .limitedQuantity(event.limitedQuantity())
                .mediaUrls(event.mediaUrls())
                .scheduledAt(event.scheduledAt())
                .createdAt(event.occurredAt())
                .build();
        releaseIndexPort.save(document);
        searchCachePort.addRecentRelease(document);
        log.info("[Kafka] Release 색인 완료. id={}", event.releaseId());
    }

    @Idempotent
    @KafkaListener(topics = "outbox.event.release-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void onUpdated(ConsumerRecord<String, ReleaseUpdatedEvent> consumerRecord) {
        ReleaseUpdatedEvent event = consumerRecord.value();
        ReleaseUpdatedEvent.ReleaseSnapshot after = event.after();
        releaseIndexPort.update(event.releaseId(), after.title(), after.description(), after.mediaUrls());
        log.info("[Kafka] Release 업데이트 완료. id={}", event.releaseId());
    }

    @Idempotent
    @KafkaListener(topics = "outbox.event.release-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onDeleted(ConsumerRecord<String, ReleaseDeletedEvent> consumerRecord) {
        ReleaseDeletedEvent event = consumerRecord.value();
        releaseIndexPort.delete(event.releaseId());
        log.info("[Kafka] Release 삭제 완료. id={}", event.releaseId());
    }
}
