package kr.magicbox.search.application.dto.command;

import lombok.Builder;

import java.util.List;

@Builder
public record UpdateReleaseCommand(
        Long releaseId,
        String title,
        String description,
        List<String> mediaUrls
) {
}
