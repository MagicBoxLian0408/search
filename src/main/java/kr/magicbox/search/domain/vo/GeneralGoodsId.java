package kr.magicbox.search.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;

public record GeneralGoodsId(@JsonValue Long value) {
    public GeneralGoodsId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("GeneralGoodsId must be positive");
        }
    }

    public static GeneralGoodsId of(Long value) {
        return new GeneralGoodsId(value);
    }
}
