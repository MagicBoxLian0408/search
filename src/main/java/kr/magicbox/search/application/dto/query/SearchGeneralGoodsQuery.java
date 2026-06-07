package kr.magicbox.search.application.dto.query;

public record SearchGeneralGoodsQuery(
        Long userId,
        String keyword,
        int page,
        int size
) {
    public static SearchGeneralGoodsQuery of(Long userId, String keyword, int page, int size) {
        return new SearchGeneralGoodsQuery(userId, keyword, page, size);
    }
}
