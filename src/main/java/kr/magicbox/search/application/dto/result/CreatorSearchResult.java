package kr.magicbox.search.application.dto.result;

import lombok.Builder;

import java.util.List;

@Builder
public record CreatorSearchResult(
        Long creatorId,
        Long userId,
        String nickname,
        String tagline,
        String profileImageUrl,
        List<String> genres,
        Long followerCount
) {
}
