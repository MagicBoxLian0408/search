package kr.magicbox.search.application.dto.query;

public record SearchCreatorsQuery(
        Long userId,
        String keyword,
        int page,
        int size
) {
    public static SearchCreatorsQuery of(Long userId, String keyword, int page, int size) {
        return new SearchCreatorsQuery(userId, keyword, page, size);
    }
}
