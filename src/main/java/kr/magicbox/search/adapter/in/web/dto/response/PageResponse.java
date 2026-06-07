package kr.magicbox.search.adapter.in.web.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalElements
) {
    public static <T> PageResponse<T> of(List<T> content, int page, int size) {
        return new PageResponse<>(content, page, size, content.size());
    }
}
