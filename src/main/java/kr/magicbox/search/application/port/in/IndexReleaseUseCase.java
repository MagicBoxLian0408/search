package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.command.DeleteReleaseCommand;
import kr.magicbox.search.application.dto.command.IndexReleaseCommand;
import kr.magicbox.search.application.dto.command.UpdateReleaseCommand;

public interface IndexReleaseUseCase {
    void indexRelease(IndexReleaseCommand command);
    void updateRelease(UpdateReleaseCommand command);
    void deleteRelease(DeleteReleaseCommand command);
}
