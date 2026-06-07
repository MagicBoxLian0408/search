package kr.magicbox.search.domain.vo;

import kr.magicbox.search.domain.exception.InvalidFieldException;

public record GeneralGoodsId(Long value) {
    public GeneralGoodsId {
        if (value == null || value <= 0) throw new InvalidFieldException("일반 상품 ID는 양수여야 합니다.");
    }

    public static GeneralGoodsId of(Long value) {
        return new GeneralGoodsId(value);
    }
}
