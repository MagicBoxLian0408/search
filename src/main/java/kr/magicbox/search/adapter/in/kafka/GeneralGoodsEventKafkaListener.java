package kr.magicbox.search.adapter.in.kafka;

import kr.magicbox.search.adapter.in.kafka.annotation.Idempotent;
import kr.magicbox.search.adapter.in.kafka.event.GeneralGoodsCreatedEvent;
import kr.magicbox.search.adapter.in.kafka.event.GeneralGoodsDeletedEvent;
import kr.magicbox.search.adapter.in.kafka.event.GeneralGoodsUpdatedEvent;
import kr.magicbox.search.adapter.out.persistence.repository.SearchInboxRepository;
import kr.magicbox.search.application.dto.command.DeleteGeneralGoodsCommand;
import kr.magicbox.search.application.dto.command.IndexGeneralGoodsCommand;
import kr.magicbox.search.application.dto.command.UpdateGeneralGoodsCommand;
import kr.magicbox.search.application.port.in.IndexGeneralGoodsUseCase;
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
public class GeneralGoodsEventKafkaListener {

    private final IndexGeneralGoodsUseCase indexGeneralGoodsUseCase;
    private final SearchInboxRepository searchInboxRepository;

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.general-goods-created", groupId = "search-service")
    public void handleGeneralGoodsCreated(ConsumerRecord<String, GeneralGoodsCreatedEvent> consumerRecord) {
        GeneralGoodsCreatedEvent event = consumerRecord.value();
        indexGeneralGoodsUseCase.indexGeneralGoods(IndexGeneralGoodsCommand.builder()
                .generalGoodsId(event.generalGoodsId())
                .creatorId(event.creatorId())
                .name(event.name())
                .description(event.description())
                .level(event.level())
                .categories(event.categories())
                .price(event.price())
                .stock(event.stock())
                .mediaUrls(event.mediaUrls())
                .build());
    }

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.general-goods-updated", groupId = "search-service")
    public void handleGeneralGoodsUpdated(ConsumerRecord<String, GeneralGoodsUpdatedEvent> consumerRecord) {
        GeneralGoodsUpdatedEvent event = consumerRecord.value();
        GeneralGoodsUpdatedEvent.GoodsSnapshot after = event.after();
        indexGeneralGoodsUseCase.updateGeneralGoods(UpdateGeneralGoodsCommand.builder()
                .generalGoodsId(event.generalGoodsId())
                .name(after.name())
                .description(after.description())
                .level(after.level())
                .categories(after.categories())
                .price(after.price())
                .stock(after.stock())
                .mediaUrls(after.mediaUrls())
                .build());
    }

    @Idempotent
    @RetryableTopic(dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "-dlt", exclude = {BusinessException.class})
    @KafkaListener(topics = "outbox.event.general-goods-deleted", groupId = "search-service")
    public void handleGeneralGoodsDeleted(ConsumerRecord<String, GeneralGoodsDeletedEvent> consumerRecord) {
        GeneralGoodsDeletedEvent event = consumerRecord.value();
        indexGeneralGoodsUseCase.deleteGeneralGoods(new DeleteGeneralGoodsCommand(event.generalGoodsId()));
    }

    @DltHandler
    public void handleDlt(ConsumerRecord<String, ?> consumerRecord) {
        log.error("[Inbox] DLT 전환. topic={}, partition={}, offset={}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
        searchInboxRepository.findByTopicAndPartitionAndOffset(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset())
                .flatMap(inbox -> searchInboxRepository.save(inbox.markDeadLettered()))
                .subscribe();
    }
}
