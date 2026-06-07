package kr.magicbox.search.application.dto.command;

import lombok.Builder;

import java.util.List;

@Builder
public record UpdateCreatorProfileCommand(
        Long creatorId,
        String nickname,
        String tagline,
        String profileImageUrl,
        String introduction,
        List<String> genres
) {
}
