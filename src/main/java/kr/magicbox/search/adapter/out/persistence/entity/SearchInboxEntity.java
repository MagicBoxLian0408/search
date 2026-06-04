package kr.magicbox.search.adapter.out.persistence.entity;

import com.github.lian2945.sonyflake.annotation.SonyflakeId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Entity
@Table(name = "search_inbox", indexes = {
        @Index(name = "idx_search_inbox_event_id", columnList = "event_id", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchInboxEntity {

    @Id
    @SonyflakeId
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private Long eventId;

    @Column(nullable = false)
    private String topic;

    @Column(name = "kafka_partition", nullable = false)
    private Integer partition;

    @Column(name = "kafka_offset", nullable = false)
    private Long offset;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Builder
    public SearchInboxEntity(Long eventId, String topic, Integer partition, Long offset, Instant occurredAt) {
        this.eventId = eventId;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.occurredAt = occurredAt;
    }
}
