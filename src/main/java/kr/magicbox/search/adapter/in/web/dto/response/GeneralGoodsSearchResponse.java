package kr.magicbox.search.adapter.in.web.dto.response;

import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record GeneralGoodsSearchResponse(
        Long generalGoodsId,
        Long creatorId,
        String name,
        Long price,
        Long stock,
        List<String> mediaUrls,
        Instant createdAt
) {
    public static GeneralGoodsSearchResponse from(GeneralGoodsDocument doc) {
        return GeneralGoodsSearchResponse.builder()
                .generalGoodsId(doc.getGeneralGoodsId())
                .creatorId(doc.getCreatorId())
                .name(doc.getName())
                .price(doc.getPrice())
                .stock(doc.getStock())
                .mediaUrls(doc.getMediaUrls())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
