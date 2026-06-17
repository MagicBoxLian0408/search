package kr.magicbox.search.application.service;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.application.dto.command.DeleteCreatorCommand;
import kr.magicbox.search.application.dto.command.IndexCreatorCommand;
import kr.magicbox.search.application.dto.command.UpdateCreatorProfileCommand;
import kr.magicbox.search.application.port.in.IndexCreatorUseCase;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class IndexCreatorService implements IndexCreatorUseCase {

    private final CreatorIndexPort creatorIndexPort;

    @Override
    public void indexCreator(IndexCreatorCommand command) {
        creatorIndexPort.findByCreatorId(command.creatorId())
                .defaultIfEmpty(CreatorDocument.builder()
                        .creatorId(command.creatorId())
                        .userId(command.userId())
                        .followerCount(0L)
                        .createdAt(Instant.now())
                        .build())
                .flatMap(existing -> {
                    CreatorDocument document = CreatorDocument.builder()
                            .id(existing.getId())
                            .creatorId(command.creatorId())
                            .userId(command.userId())
                            .nickname(command.nickname())
                            .profileImageUrl(command.profileImageUrl())
                            .genres(command.genres())
                            .followerCount(existing.getFollowerCount())
                            .status(command.status())
                            .createdAt(existing.getCreatedAt() != null ? existing.getCreatedAt() : Instant.now())
                            .build();
                    return creatorIndexPort.save(document);
                })
                .subscribe();
    }

    @Override
    public void updateCreatorProfile(UpdateCreatorProfileCommand command) {
        creatorIndexPort.updateProfile(
                command.creatorId(),
                command.nickname(),
                command.tagline(),
                command.profileImageUrl(),
                command.introduction(),
                command.genres()
        ).subscribe();
    }

    @Override
    public void deleteCreator(DeleteCreatorCommand command) {
        creatorIndexPort.updateStatus(command.creatorId(), "DELETED").subscribe();
    }

    @Override
    public void incrementFollowerCount(Long creatorId) {
        creatorIndexPort.incrementFollowerCount(creatorId).subscribe();
    }

    @Override
    public void decrementFollowerCount(Long creatorId) {
        creatorIndexPort.decrementFollowerCount(creatorId).subscribe();
    }
}
