package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GeneralGoodsIndexPort {
    Mono<Void> save(GeneralGoodsDocument document);
    Mono<Void> updateGeneralGoods(Long generalGoodsId, String name, String description, String level, List<String> categories, Long price, Long stock, List<String> mediaUrls);
    Mono<Void> softDelete(Long generalGoodsId);
    Mono<GeneralGoodsDocument> findByGeneralGoodsId(Long generalGoodsId);
    Mono<List<GeneralGoodsDocument>> searchByKeyword(String keyword, int page, int size);
    Mono<List<GeneralGoodsDocument>> findPopular(int size);
    Mono<List<GeneralGoodsDocument>> findRecent(int size);
    Mono<List<String>> suggest(String keyword);
}
