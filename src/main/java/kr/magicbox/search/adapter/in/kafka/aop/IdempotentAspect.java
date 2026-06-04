package kr.magicbox.search.adapter.in.kafka.aop;

import kr.magicbox.search.adapter.in.kafka.event.InboxEvent;
import kr.magicbox.search.adapter.in.kafka.properties.InboxProperties;
import kr.magicbox.search.adapter.out.persistence.entity.SearchInboxEntity;
import kr.magicbox.search.adapter.out.persistence.repository.SearchInboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final SearchInboxRepository searchInboxRepository;
    private final TransactionTemplate transactionTemplate;
    private final InboxProperties inboxProperties;

    @Around("@annotation(kr.magicbox.search.adapter.in.kafka.annotation.Idempotent)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        ConsumerRecord<String, ?> consumerRecord = extractRecord(pjp);
        InboxEvent event = (InboxEvent) consumerRecord.value();
        Long eventId = Long.parseLong(consumerRecord.key());
        Instant occurredAt = event.occurredAt();

        if (isTooOld(occurredAt)) {
            log.warn("[Inbox] 만료된 메시지 폐기. eventId={}, occurredAt={}", eventId, occurredAt);
            return null;
        }

        return transactionTemplate.execute(status -> {
            if (searchInboxRepository.existsByEventId(eventId)) {
                log.warn("[Inbox] 중복 메시지 폐기. eventId={}", eventId);
                return null;
            }

            searchInboxRepository.save(SearchInboxEntity.builder()
                    .eventId(eventId)
                    .topic(consumerRecord.topic())
                    .partition(consumerRecord.partition())
                    .offset(consumerRecord.offset())
                    .occurredAt(occurredAt)
                    .build());

            try {
                return pjp.proceed();
            } catch (Throwable e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private ConsumerRecord<String, ?> extractRecord(ProceedingJoinPoint pjp) {
        for (Object arg : pjp.getArgs()) {
            if (arg instanceof ConsumerRecord<?, ?> record) {
                return (ConsumerRecord<String, ?>) record;
            }
        }
        throw new IllegalArgumentException("ConsumerRecord not found in method arguments");
    }

    private boolean isTooOld(Instant occurredAt) {
        return occurredAt.isBefore(
                Instant.now().minus(inboxProperties.getMaxEventAgeMinutes(), ChronoUnit.MINUTES));
    }
}
