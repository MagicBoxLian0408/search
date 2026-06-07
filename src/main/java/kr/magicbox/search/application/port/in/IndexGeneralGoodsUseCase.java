package kr.magicbox.search.application.port.in;

import kr.magicbox.search.application.dto.command.DeleteGeneralGoodsCommand;
import kr.magicbox.search.application.dto.command.IndexGeneralGoodsCommand;
import kr.magicbox.search.application.dto.command.UpdateGeneralGoodsCommand;

public interface IndexGeneralGoodsUseCase {
    void indexGeneralGoods(IndexGeneralGoodsCommand command);
    void updateGeneralGoods(UpdateGeneralGoodsCommand command);
    void deleteGeneralGoods(DeleteGeneralGoodsCommand command);
}
