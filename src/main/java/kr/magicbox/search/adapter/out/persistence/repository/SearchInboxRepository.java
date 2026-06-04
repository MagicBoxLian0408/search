package kr.magicbox.search.adapter.out.persistence.repository;

import kr.magicbox.search.adapter.out.persistence.entity.SearchInboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchInboxRepository extends JpaRepository<SearchInboxEntity, Long> {
    boolean existsByEventId(Long eventId);
}
