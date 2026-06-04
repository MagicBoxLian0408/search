package kr.magicbox.search.application.port.in;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;

import java.util.List;

public interface PopularQueryUseCase {
    List<CreatorDocument> getPopularCreators();
    List<ReleaseDocument> getPopularReleases();
    List<GeneralGoodsDocument> getPopularGeneralGoods();
}
