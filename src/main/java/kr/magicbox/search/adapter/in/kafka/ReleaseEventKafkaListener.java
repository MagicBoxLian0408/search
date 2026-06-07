package kr.magicbox.search.adapter.in.kafka;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseCreatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseDeletedEvent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseUpdatedEvent;
import kr.magicbox.search.adapter.out.persistence.repository.SearchInboxRepository;
import kr.magicbox.search.application.dto.command.DeleteReleaseCommand;
import kr.magicbox.search.application.dto.command.IndexReleaseCommand;
import kr.magicbox.search.application.dto.command.UpdateReleaseCommand;
import kr.magicbox.search.application.port.in.IndexReleaseUseCase;
import kr.magicbox.search.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseEventKafkaListener {

    private final IndexReleaseUseCase indexReleaseUseCase;
    private final SearchInboxRepository searchInboxRepository;

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.release-created", groupId = "search-service")
    public void handleReleaseCreated(ConsumerRecord<String, ReleaseCreatedEvent> consumerRecord) {
        ReleaseCreatedEvent event = consumerRecord.value();
        indexReleaseUseCase.indexRelease(IndexReleaseCommand.builder()
                .releaseId(event.releaseId())
                .creatorId(event.creatorId())
                .title(event.title())
                .description(event.description())
                .level(event.level())
                .status(event.status())
                .price(event.price())
                .limitedQuantity(event.limitedQuantity())
                .scheduledAt(event.scheduledAt())
                .mediaUrls(event.mediaUrls())
                .build());
    }

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.release-updated", groupId = "search-service")
    public void handleReleaseUpdated(ConsumerRecord<String, ReleaseUpdatedEvent> consumerRecord) {
        ReleaseUpdatedEvent event = consumerRecord.value();
        ReleaseUpdatedEvent.ReleaseSnapshot after = event.after();
        indexReleaseUseCase.updateRelease(UpdateReleaseCommand.builder()
                .releaseId(event.releaseId())
                .title(after.title())
                .description(after.description())
                .mediaUrls(after.mediaUrls())
                .build());
    }

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.release-deleted", groupId = "search-service")
    public void handleReleaseDeleted(ConsumerRecord<String, ReleaseDeletedEvent> consumerRecord) {
        ReleaseDeletedEvent event = consumerRecord.value();
        indexReleaseUseCase.deleteRelease(new DeleteReleaseCommand(event.releaseId()));
    }

    @DltHandler
    public void handleDlt(ConsumerRecord<String, ?> consumerRecord) {
        log.error("[Inbox] DLT 전환. topic={}, partition={}, offset={}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
        searchInboxRepository.findByTopicAndPartitionAndOffset(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset())
                .flatMap(inbox -> searchInboxRepository.save(inbox.markDeadLettered()))
                .subscribe();
    }
}
