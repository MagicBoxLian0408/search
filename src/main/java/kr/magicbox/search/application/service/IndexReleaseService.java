package kr.magicbox.search.application.service;

import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.dto.command.DeleteReleaseCommand;
import kr.magicbox.search.application.dto.command.IndexReleaseCommand;
import kr.magicbox.search.application.dto.command.UpdateReleaseCommand;
import kr.magicbox.search.application.port.in.IndexReleaseUseCase;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class IndexReleaseService implements IndexReleaseUseCase {

    private final ReleaseIndexPort releaseIndexPort;

    @Override
    public void indexRelease(IndexReleaseCommand command) {
        ReleaseDocument document = ReleaseDocument.builder()
                .releaseId(command.releaseId())
                .creatorId(command.creatorId())
                .title(command.title())
                .description(command.description())
                .level(command.level())
                .status(command.status())
                .price(command.price())
                .limitedQuantity(command.limitedQuantity())
                .scheduledAt(command.scheduledAt())
                .mediaUrls(command.mediaUrls())
                .likeCount(0L)
                .createdAt(Instant.now())
                .build();
        releaseIndexPort.save(document).subscribe();
    }

    @Override
    public void updateRelease(UpdateReleaseCommand command) {
        releaseIndexPort.updateRelease(
                command.releaseId(),
                command.title(),
                command.description(),
                command.mediaUrls()
        ).subscribe();
    }

    @Override
    public void deleteRelease(DeleteReleaseCommand command) {
        releaseIndexPort.deleteByReleaseId(command.releaseId()).subscribe();
    }
}
