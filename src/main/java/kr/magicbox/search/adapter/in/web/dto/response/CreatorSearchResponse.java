package kr.magicbox.search.adapter.in.web.dto.response;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record CreatorSearchResponse(
        Long creatorId,
        String nickname,
        String tagline,
        String profileImageUrl,
        List<String> genres,
        Instant createdAt
) {
    public static CreatorSearchResponse from(CreatorDocument doc) {
        return CreatorSearchResponse.builder()
                .creatorId(doc.getCreatorId())
                .nickname(doc.getNickname())
                .tagline(doc.getTagline())
                .profileImageUrl(doc.getProfileImageUrl())
                .genres(doc.getGenres())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
