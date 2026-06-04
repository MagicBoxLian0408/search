package kr.magicbox.search.application.port.out;

import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;

import java.util.List;

public interface GeneralGoodsIndexPort {
    void save(GeneralGoodsDocument document);
    void update(Long generalGoodsId, String name, Long price, Long stock, List<String> mediaUrls);
    void delete(Long generalGoodsId);
    List<GeneralGoodsDocument> search(String keyword, int page, int size);
    List<GeneralGoodsDocument> findPopular(int size);
    List<GeneralGoodsDocument> findRecent(int size);
}
