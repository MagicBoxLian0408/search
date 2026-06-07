package kr.magicbox.search.application.dto.query;

public record SearchReleasesQuery(
        Long userId,
        String keyword,
        int page,
        int size
) {
    public static SearchReleasesQuery of(Long userId, String keyword, int page, int size) {
        return new SearchReleasesQuery(userId, keyword, page, size);
    }
}
