package kr.magicbox.search.adapter.in.kafka.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfiguration {

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<?, ?> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (ConsumerRecord<?, ?> failedRecord, Exception ex) -> {
                    String topic = failedRecord.topic() + "-dlt";
                    log.error("[DLT] 메시지 처리 실패, DLT 전송합니다. topic={}, offset={}, exception={}",
                            failedRecord.topic(), failedRecord.offset(), ex.getMessage());
                    return new TopicPartition(topic, failedRecord.partition());
                });
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }
}
