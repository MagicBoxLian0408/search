package kr.magicbox.search.adapter.in.kafka;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.SubscriberCreatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.SubscriberDeletedEvent;
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
public class SubscribeEventKafkaListener {

    private final IndexCreatorUseCase indexCreatorUseCase;

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.subscriber-created", groupId = "search-service")
    public void handleSubscriberCreated(ConsumerRecord<String, SubscriberCreatedEvent> consumerRecord) {
        SubscriberCreatedEvent event = consumerRecord.value();
        indexCreatorUseCase.incrementFollowerCount(event.creatorId());
    }

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.subscriber-deleted", groupId = "search-service")
    public void handleSubscriberDeleted(ConsumerRecord<String, SubscriberDeletedEvent> consumerRecord) {
        SubscriberDeletedEvent event = consumerRecord.value();
        indexCreatorUseCase.decrementFollowerCount(event.creatorId());
    }
}
