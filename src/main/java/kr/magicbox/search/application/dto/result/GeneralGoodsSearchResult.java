package kr.magicbox.search.application.dto.result;

import lombok.Builder;

import java.util.List;

@Builder
public record GeneralGoodsSearchResult(
        Long generalGoodsId,
        Long creatorId,
        String name,
        String description,
        String level,
        List<String> categories,
        Long price,
        Long stock,
        List<String> mediaUrls,
        Long likeCount
) {
}
