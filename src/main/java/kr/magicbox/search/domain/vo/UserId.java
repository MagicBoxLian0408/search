package kr.magicbox.search.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;

public record UserId(@JsonValue Long value) {
    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UserId must be positive");
        }
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }
}
