package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.command.DeleteCreatorCommand;
import kr.magicbox.search.application.dto.command.IndexCreatorCommand;
import kr.magicbox.search.application.dto.command.UpdateCreatorProfileCommand;

public interface IndexCreatorUseCase {
    void indexCreator(IndexCreatorCommand command);
    void updateCreatorProfile(UpdateCreatorProfileCommand command);
    void deleteCreator(DeleteCreatorCommand command);
    void incrementFollowerCount(Long creatorId);
    void decrementFollowerCount(Long creatorId);
}
