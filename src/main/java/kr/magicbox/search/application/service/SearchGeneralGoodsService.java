package kr.magicbox.search.application.service;

import kr.magicbox.search.application.dto.query.SearchGeneralGoodsQuery;
import kr.magicbox.search.application.dto.result.GeneralGoodsSearchResult;
import kr.magicbox.search.application.port.in.SearchGeneralGoodsUseCase;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchGeneralGoodsService implements SearchGeneralGoodsUseCase {

    private final GeneralGoodsIndexPort generalGoodsIndexPort;
    private final SearchCachePort searchCachePort;

    @Override
    public Mono<List<GeneralGoodsSearchResult>> searchGeneralGoods(SearchGeneralGoodsQuery query) {
        return searchCachePort.addSearchQuery(query.userId(), query.keyword())
                .then(searchCachePort.incrementQueryScore(query.keyword()))
                .then(generalGoodsIndexPort.searchByKeyword(query.keyword(), query.page(), query.size()))
                .map(docs -> docs.stream().map(doc -> GeneralGoodsSearchResult.builder()
                        .generalGoodsId(doc.getGeneralGoodsId())
                        .creatorId(doc.getCreatorId())
                        .name(doc.getName())
                        .description(doc.getDescription())
                        .level(doc.getLevel())
                        .categories(doc.getCategories())
                        .price(doc.getPrice())
                        .stock(doc.getStock())
                        .mediaUrls(doc.getMediaUrls())
                        .likeCount(doc.getLikeCount())
                        .build()).toList());
    }
}
