package kr.magicbox.search.adapter.in.web.dto.response;

import kr.magicbox.search.application.dto.result.CreatorSearchResult;
import lombok.Builder;

import java.util.List;

@Builder
public record CreatorSearchResponse(
        Long creatorId,
        Long userId,
        String nickname,
        String tagline,
        String profileImageUrl,
        List<String> genres,
        Long followerCount
) {
    public static CreatorSearchResponse from(CreatorSearchResult result) {
        return CreatorSearchResponse.builder()
                .creatorId(result.creatorId())
                .userId(result.userId())
                .nickname(result.nickname())
                .tagline(result.tagline())
                .profileImageUrl(result.profileImageUrl())
                .genres(result.genres())
                .followerCount(result.followerCount())
                .build();
    }
}
