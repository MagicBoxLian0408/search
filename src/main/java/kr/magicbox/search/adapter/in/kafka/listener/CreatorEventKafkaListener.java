package kr.magicbox.search.adapter.in.kafka.listener;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.CreatorCertificationApprovedEvent;
import kr.magicbox.search.adapter.in.kafka.event.CreatorProfileUpdatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.CreatorRevokedEvent;
import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatorEventKafkaListener {

    private final CreatorIndexPort creatorIndexPort;
    private final SearchCachePort searchCachePort;

    @Idempotent
    @KafkaListener(topics = "outbox.event.creator-certification-approved", groupId = "${spring.kafka.consumer.group-id}")
    public void onApproved(ConsumerRecord<String, CreatorCertificationApprovedEvent> consumerRecord) {
        CreatorCertificationApprovedEvent event = consumerRecord.value();
        CreatorDocument document = CreatorDocument.builder()
                .creatorId(event.userId())
                .createdAt(event.occurredAt())
                .build();
        creatorIndexPort.save(document);
        searchCachePort.addRecentCreator(document);
        log.info("[Kafka] Creator 색인 완료. userId={}", event.userId());
    }

    @Idempotent
    @KafkaListener(topics = "outbox.event.creator-profile-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void onProfileUpdated(ConsumerRecord<String, CreatorProfileUpdatedEvent> consumerRecord) {
        CreatorProfileUpdatedEvent event = consumerRecord.value();
        CreatorProfileUpdatedEvent.ProfileSnapshot after = event.after();
        creatorIndexPort.update(event.creatorId(), after.nickname(), after.tagline(), after.profileImageUrl());
        log.info("[Kafka] Creator 프로필 업데이트 완료. id={}", event.creatorId());
    }

    @Idempotent
    @KafkaListener(topics = "outbox.event.creator-revoked", groupId = "${spring.kafka.consumer.group-id}")
    public void onRevoked(ConsumerRecord<String, CreatorRevokedEvent> consumerRecord) {
        CreatorRevokedEvent event = consumerRecord.value();
        creatorIndexPort.delete(event.creatorId());
        log.info("[Kafka] Creator 삭제 완료. id={}", event.creatorId());
    }
}
