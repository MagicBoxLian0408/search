package kr.magicbox.search.adapter.in.web;

import kr.magicbox.search.adapter.in.web.constants.CursorConstants;
import kr.magicbox.search.adapter.in.web.dto.response.CreatorSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.GeneralGoodsSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.PageResponse;
import kr.magicbox.search.adapter.in.web.dto.response.ReleaseSearchResponse;
import kr.magicbox.search.adapter.in.web.validation.CursorSize;
import kr.magicbox.search.application.port.in.HistoryUseCase;
import kr.magicbox.search.application.port.in.PopularQueryUseCase;
import kr.magicbox.search.application.port.in.RecentQueryUseCase;
import kr.magicbox.search.application.port.in.SearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
public class SearchController {

    private final SearchUseCase searchUseCase;
    private final PopularQueryUseCase popularQueryUseCase;
    private final RecentQueryUseCase recentQueryUseCase;
    private final HistoryUseCase historyUseCase;

    // ===== 검색 =====

    @GetMapping("/creators")
    public ResponseEntity<PageResponse<CreatorSearchResponse>> searchCreators(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(PageResponse.of(
                searchUseCase.searchCreators(keyword, page, size).stream()
                        .map(CreatorSearchResponse::from).toList(),
                page, size));
    }

    @GetMapping("/releases")
    public ResponseEntity<PageResponse<ReleaseSearchResponse>> searchReleases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(PageResponse.of(
                searchUseCase.searchReleases(keyword, page, size).stream()
                        .map(ReleaseSearchResponse::from).toList(),
                page, size));
    }

    @GetMapping("/general-goods")
    public ResponseEntity<PageResponse<GeneralGoodsSearchResponse>> searchGeneralGoods(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(PageResponse.of(
                searchUseCase.searchGeneralGoods(keyword, page, size).stream()
                        .map(GeneralGoodsSearchResponse::from).toList(),
                page, size));
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

    // ===== 인기 =====

    @GetMapping("/popular/creators")
    public ResponseEntity<PageResponse<CreatorSearchResponse>> getPopularCreators() {
        List<CreatorSearchResponse> content = popularQueryUseCase.getPopularCreators().stream()
                .map(CreatorSearchResponse::from).toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    @GetMapping("/popular/releases")
    public ResponseEntity<PageResponse<ReleaseSearchResponse>> getPopularReleases() {
        List<ReleaseSearchResponse> content = popularQueryUseCase.getPopularReleases().stream()
                .map(ReleaseSearchResponse::from).toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    @GetMapping("/popular/general-goods")
    public ResponseEntity<PageResponse<GeneralGoodsSearchResponse>> getPopularGeneralGoods() {
        List<GeneralGoodsSearchResponse> content = popularQueryUseCase.getPopularGeneralGoods().stream()
                .map(GeneralGoodsSearchResponse::from).toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    // ===== 최신 =====

    @GetMapping("/recent/creators")
    public ResponseEntity<List<CreatorSearchResponse>> getRecentCreators() {
        return ResponseEntity.ok(recentQueryUseCase.getRecentCreators().stream()
                .map(CreatorSearchResponse::from).toList());
    }

    @GetMapping("/recent/releases")
    public ResponseEntity<List<ReleaseSearchResponse>> getRecentReleases() {
        return ResponseEntity.ok(recentQueryUseCase.getRecentReleases().stream()
                .map(ReleaseSearchResponse::from).toList());
    }

    @GetMapping("/recent/general-goods")
    public ResponseEntity<List<GeneralGoodsSearchResponse>> getRecentGeneralGoods() {
        return ResponseEntity.ok(recentQueryUseCase.getRecentGeneralGoods().stream()
                .map(GeneralGoodsSearchResponse::from).toList());
    }

    // ===== 이력 =====

    @PostMapping("/history/creators/{creatorId}")
    public ResponseEntity<Void> recordViewedCreator(
            @RequestAttribute Long userId,
            @PathVariable Long creatorId
    ) {
        historyUseCase.recordViewedCreator(userId, creatorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/creators")
    public ResponseEntity<List<CreatorSearchResponse>> getViewedCreators(@RequestAttribute Long userId) {
        return ResponseEntity.ok(historyUseCase.getViewedCreators(userId).stream()
                .map(CreatorSearchResponse::from).toList());
    }

    @GetMapping("/history/queries")
    public ResponseEntity<List<String>> getSearchQueries(@RequestAttribute Long userId) {
        return ResponseEntity.ok(historyUseCase.getSearchQueries(userId));
    }
}
