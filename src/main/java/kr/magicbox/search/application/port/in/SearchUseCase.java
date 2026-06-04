package kr.magicbox.search.application.port.in;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;

import java.util.List;

public interface SearchUseCase {
    List<CreatorDocument> searchCreators(String keyword, int page, int size);
    List<ReleaseDocument> searchReleases(String keyword, int page, int size);
    List<GeneralGoodsDocument> searchGeneralGoods(String keyword, int page, int size);
    SearchAllResult searchAll(String keyword, int page, int size);

    record SearchAllResult(
            List<CreatorDocument> creators,
            List<ReleaseDocument> releases,
            List<GeneralGoodsDocument> generalGoods
    ) {}
}
