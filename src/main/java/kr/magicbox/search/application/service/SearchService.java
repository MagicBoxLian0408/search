package kr.magicbox.search.application.service;

import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.port.in.SearchUseCase;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService implements SearchUseCase {

    private final CreatorIndexPort creatorIndexPort;
    private final ReleaseIndexPort releaseIndexPort;
    private final GeneralGoodsIndexPort generalGoodsIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public List<CreatorDocument> searchCreators(String keyword, int page, int size) {
        return creatorIndexPort.search(keyword, page, size + 1);
    }

    @Override
    public List<ReleaseDocument> searchReleases(String keyword, int page, int size) {
        return releaseIndexPort.search(keyword, page, size + 1);
    }

    @Override
    public List<GeneralGoodsDocument> searchGeneralGoods(String keyword, int page, int size) {
        return generalGoodsIndexPort.search(keyword, page, size + 1);
    }

    @Override
    public SearchAllResult searchAll(String keyword, int page, int size) {
        List<CreatorDocument> creators = creatorIndexPort.search(keyword, page, size + 1);
        List<ReleaseDocument> releases = releaseIndexPort.search(keyword, page, size + 1);
        List<GeneralGoodsDocument> generalGoods = generalGoodsIndexPort.search(keyword, page, size + 1);
        return new SearchAllResult(creators, releases, generalGoods);
    }
}
