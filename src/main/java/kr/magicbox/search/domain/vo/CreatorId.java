package kr.magicbox.search.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;

public record CreatorId(@JsonValue Long value) {
    public CreatorId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CreatorId must be positive");
        }
    }

    public static CreatorId of(Long value) {
        return new CreatorId(value);
    }
}
