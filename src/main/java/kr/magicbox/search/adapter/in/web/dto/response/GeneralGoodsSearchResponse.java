package kr.magicbox.search.adapter.in.web.dto.response;

import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import lombok.Builder;

import java.util.List;

@Builder
public record GeneralGoodsSearchResponse(
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
    public static GeneralGoodsSearchResponse from(GeneralGoodsSearchResult result) {
        return GeneralGoodsSearchResponse.builder()
                .generalGoodsId(result.generalGoodsId())
                .creatorId(result.creatorId())
                .name(result.name())
                .description(result.description())
                .level(result.level())
                .categories(result.categories())
                .price(result.price())
                .stock(result.stock())
                .mediaUrls(result.mediaUrls())
                .likeCount(result.likeCount())
                .build();
    }
}
