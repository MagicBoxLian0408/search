package kr.magicbox.search.adapter.in.kafka;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseCreatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseDeletedEvent;
import kr.magicbox.search.adapter.in.kafka.event.ReleaseUpdatedEvent;
import kr.magicbox.search.application.dto.command.DeleteReleaseCommand;
import kr.magicbox.search.application.dto.command.IndexReleaseCommand;
import kr.magicbox.search.application.dto.command.UpdateReleaseCommand;
import kr.magicbox.search.application.port.in.IndexReleaseUseCase;
import kr.magicbox.search.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReleaseEventKafkaListener {

    private final IndexReleaseUseCase indexReleaseUseCase;

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

}
