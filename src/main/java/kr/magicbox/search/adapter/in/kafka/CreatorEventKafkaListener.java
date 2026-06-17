package kr.magicbox.search.adapter.in.kafka;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.CreatorCertificationApprovedEvent;
import kr.magicbox.search.adapter.in.kafka.event.CreatorProfileUpdatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.CreatorRevokedEvent;
import kr.magicbox.search.application.dto.command.DeleteCreatorCommand;
import kr.magicbox.search.application.dto.command.IndexCreatorCommand;
import kr.magicbox.search.application.dto.command.UpdateCreatorProfileCommand;
import kr.magicbox.search.application.port.in.IndexCreatorUseCase;
import kr.magicbox.search.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatorEventKafkaListener {

    private final IndexCreatorUseCase indexCreatorUseCase;

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.creator-certification-approved", groupId = "search-service")
    public void handleCreatorCertificationApproved(ConsumerRecord<String, CreatorCertificationApprovedEvent> consumerRecord) {
        CreatorCertificationApprovedEvent event = consumerRecord.value();
        indexCreatorUseCase.indexCreator(IndexCreatorCommand.builder()
                .creatorId(event.creatorId())
                .userId(event.userId())
                .nickname(event.nickname())
                .genres(event.genres())
                .status(event.status())
                .build());
    }

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.creator-profile-updated", groupId = "search-service")
    public void handleCreatorProfileUpdated(ConsumerRecord<String, CreatorProfileUpdatedEvent> consumerRecord) {
        CreatorProfileUpdatedEvent event = consumerRecord.value();
        CreatorProfileUpdatedEvent.ProfileSnapshot after = event.after();
        indexCreatorUseCase.updateCreatorProfile(UpdateCreatorProfileCommand.builder()
                .creatorId(event.creatorId())
                .nickname(after.nickname())
                .tagline(after.tagline())
                .profileImageUrl(after.profileImageUrl())
                .introduction(after.introduction())
                .genres(after.genres())
                .build());
    }

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.creator-revoked", groupId = "search-service")
    public void handleCreatorRevoked(ConsumerRecord<String, CreatorRevokedEvent> consumerRecord) {
        CreatorRevokedEvent event = consumerRecord.value();
        indexCreatorUseCase.deleteCreator(new DeleteCreatorCommand(event.creatorId()));
    }

}
