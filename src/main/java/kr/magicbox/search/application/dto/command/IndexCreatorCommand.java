package kr.magicbox.search.application.dto.command;

import lombok.Builder;

import java.util.List;

@Builder
public record IndexCreatorCommand(
        Long creatorId,
        Long userId,
        String nickname,
        List<String> genres,
        String status
) {
}
