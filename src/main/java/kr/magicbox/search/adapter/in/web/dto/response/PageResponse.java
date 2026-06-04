package kr.magicbox.search.adapter.in.web.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        boolean hasNext
) {
    public static <T> PageResponse<T> of(List<T> content, int page, int size) {
        boolean hasNext = content.size() > size;
        List<T> sliced = hasNext ? content.subList(0, size) : content;
        return PageResponse.<T>builder()
                .content(sliced)
                .page(page)
                .size(sliced.size())
                .hasNext(hasNext)
                .build();
    }
}
