package kr.magicbox.search.application.dto.result;

import java.util.List;

public record SearchAllResult(
        List<CreatorSearchResult> creators,
        List<ReleaseSearchResult> releases,
        List<GeneralGoodsSearchResult> generalGoods
) {
}
