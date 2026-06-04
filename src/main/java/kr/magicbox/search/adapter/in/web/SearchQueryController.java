package kr.magicbox.search.adapter.in.web;

import kr.magicbox.search.adapter.in.web.constants.CursorConstants;
import kr.magicbox.search.adapter.in.web.dto.response.CreatorSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.GeneralGoodsSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.PageResponse;
import kr.magicbox.search.adapter.in.web.dto.response.ReleaseSearchResponse;
import kr.magicbox.search.adapter.in.web.validation.CursorSize;
import kr.magicbox.search.application.port.in.SearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Validated
public class SearchQueryController {

    private final SearchUseCase searchUseCase;

    @GetMapping("/creators")
    public ResponseEntity<PageResponse<CreatorSearchResponse>> searchCreators(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(
                PageResponse.of(
                        searchUseCase.searchCreators(keyword, page, size).stream()
                                .map(CreatorSearchResponse::from)
                                .toList(),
                        page, size
                )
        );
    }

    @GetMapping("/releases")
    public ResponseEntity<PageResponse<ReleaseSearchResponse>> searchReleases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(
                PageResponse.of(
                        searchUseCase.searchReleases(keyword, page, size).stream()
                                .map(ReleaseSearchResponse::from)
                                .toList(),
                        page, size
                )
        );
    }

    @GetMapping("/general-goods")
    public ResponseEntity<PageResponse<GeneralGoodsSearchResponse>> searchGeneralGoods(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(
                PageResponse.of(
                        searchUseCase.searchGeneralGoods(keyword, page, size).stream()
                                .map(GeneralGoodsSearchResponse::from)
                                .toList(),
                        page, size
                )
        );
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> searchAll(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        SearchUseCase.SearchAllResult result = searchUseCase.searchAll(keyword, page, size);
        return ResponseEntity.ok(Map.of(
                "creators", PageResponse.of(result.creators().stream().map(CreatorSearchResponse::from).toList(), page, size),
                "releases", PageResponse.of(result.releases().stream().map(ReleaseSearchResponse::from).toList(), page, size),
                "generalGoods", PageResponse.of(result.generalGoods().stream().map(GeneralGoodsSearchResponse::from).toList(), page, size)
        ));
    }
}
