package kr.magicbox.search.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;

public record ReleaseId(@JsonValue Long value) {
    public ReleaseId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ReleaseId must be positive");
        }
    }

    public static ReleaseId of(Long value) {
        return new ReleaseId(value);
    }
}
