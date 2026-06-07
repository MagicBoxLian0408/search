package kr.magicbox.search.application.service;

import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.application.dto.command.DeleteGeneralGoodsCommand;
import kr.magicbox.search.application.dto.command.IndexGeneralGoodsCommand;
import kr.magicbox.search.application.dto.command.UpdateGeneralGoodsCommand;
import kr.magicbox.search.application.port.in.IndexGeneralGoodsUseCase;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class IndexGeneralGoodsService implements IndexGeneralGoodsUseCase {

    private final GeneralGoodsIndexPort generalGoodsIndexPort;

    @Override
    public void indexGeneralGoods(IndexGeneralGoodsCommand command) {
        GeneralGoodsDocument document = GeneralGoodsDocument.builder()
                .generalGoodsId(command.generalGoodsId())
                .creatorId(command.creatorId())
                .name(command.name())
                .description(command.description())
                .level(command.level())
                .categories(command.categories())
                .price(command.price())
                .stock(command.stock())
                .mediaUrls(command.mediaUrls())
                .likeCount(0L)
                .isDeleted(false)
                .createdAt(Instant.now())
                .build();
        generalGoodsIndexPort.save(document).subscribe();
    }

    @Override
    public void updateGeneralGoods(UpdateGeneralGoodsCommand command) {
        generalGoodsIndexPort.updateGeneralGoods(
                command.generalGoodsId(),
                command.name(),
                command.description(),
                command.level(),
                command.categories(),
                command.price(),
                command.stock(),
                command.mediaUrls()
        ).subscribe();
    }

    @Override
    public void deleteGeneralGoods(DeleteGeneralGoodsCommand command) {
        generalGoodsIndexPort.softDelete(command.generalGoodsId()).subscribe();
    }
}
