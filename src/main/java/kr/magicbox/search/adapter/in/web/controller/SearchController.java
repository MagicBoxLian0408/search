package kr.magicbox.search.adapter.in.web;

import kr.magicbox.search.adapter.in.web.constants.CursorConstants;
import kr.magicbox.search.adapter.in.web.dto.response.CreatorSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.GeneralGoodsSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.PageResponse;
import kr.magicbox.search.adapter.in.web.dto.response.ReleaseSearchResponse;
import kr.magicbox.search.adapter.in.web.validation.CursorSize;
import kr.magicbox.search.application.dto.query.SearchCreatorsQuery;
import kr.magicbox.search.application.dto.query.SearchGeneralGoodsQuery;
import kr.magicbox.search.application.dto.query.SearchReleasesQuery;
import kr.magicbox.search.application.dto.result.SearchAllResult;
import kr.magicbox.search.application.port.in.HistoryUseCase;
import kr.magicbox.search.application.port.in.PopularQueryUseCase;
import kr.magicbox.search.application.port.in.RecentQueryUseCase;
import kr.magicbox.search.application.port.in.SearchAllUseCase;
import kr.magicbox.search.application.port.in.SearchCreatorsUseCase;
import kr.magicbox.search.application.port.in.SearchGeneralGoodsUseCase;
import kr.magicbox.search.application.port.in.SearchReleasesUseCase;
import kr.magicbox.search.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
public class SearchController {

    private final SearchCreatorsUseCase searchCreatorsUseCase;
    private final SearchReleasesUseCase searchReleasesUseCase;
    private final SearchGeneralGoodsUseCase searchGeneralGoodsUseCase;
    private final SearchAllUseCase searchAllUseCase;
    private final PopularQueryUseCase popularQueryUseCase;
    private final RecentQueryUseCase recentQueryUseCase;
    private final HistoryUseCase historyUseCase;

    // ===== 검색 =====

    @GetMapping("/creators")
    public ResponseEntity<PageResponse<CreatorSearchResponse>> searchCreators(
            @AuthenticationPrincipal UserId userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(PageResponse.of(
                searchCreatorsUseCase.searchCreators(SearchCreatorsQuery.of(userId.value(), keyword, page, size)).stream()
                        .map(CreatorSearchResponse::from).toList(),
                page, size));
    }

    @GetMapping("/releases")
    public ResponseEntity<PageResponse<ReleaseSearchResponse>> searchReleases(
            @AuthenticationPrincipal UserId userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(PageResponse.of(
                searchReleasesUseCase.searchReleases(SearchReleasesQuery.of(userId.value(), keyword, page, size)).stream()
                        .map(ReleaseSearchResponse::from).toList(),
                page, size));
    }

    @GetMapping("/general-goods")
    public ResponseEntity<PageResponse<GeneralGoodsSearchResponse>> searchGeneralGoods(
            @AuthenticationPrincipal UserId userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        return ResponseEntity.ok(PageResponse.of(
                searchGeneralGoodsUseCase.searchGeneralGoods(SearchGeneralGoodsQuery.of(userId.value(), keyword, page, size)).stream()
                        .map(GeneralGoodsSearchResponse::from).toList(),
                page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> searchAll(
            @AuthenticationPrincipal UserId userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @CursorSize @RequestParam(defaultValue = CursorConstants.DEFAULT_SIZE) int size
    ) {
        SearchAllResult result = searchAllUseCase.searchAll(SearchCreatorsQuery.of(userId.value(), keyword, page, size));
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
    public ResponseEntity<PageResponse<CreatorSearchResponse>> getRecentCreators() {
        List<CreatorSearchResponse> content = recentQueryUseCase.getRecentCreators().stream()
                .map(CreatorSearchResponse::from).toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    @GetMapping("/recent/releases")
    public ResponseEntity<PageResponse<ReleaseSearchResponse>> getRecentReleases() {
        List<ReleaseSearchResponse> content = recentQueryUseCase.getRecentReleases().stream()
                .map(ReleaseSearchResponse::from).toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    @GetMapping("/recent/general-goods")
    public ResponseEntity<PageResponse<GeneralGoodsSearchResponse>> getRecentGeneralGoods() {
        List<GeneralGoodsSearchResponse> content = recentQueryUseCase.getRecentGeneralGoods().stream()
                .map(GeneralGoodsSearchResponse::from).toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    // ===== 이력 =====

    @PostMapping("/history/creators/{creatorId}")
    public ResponseEntity<Void> recordViewedCreator(
            @AuthenticationPrincipal UserId userId,
            @PathVariable Long creatorId
    ) {
        historyUseCase.recordViewedCreator(userId.value(), creatorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/creators")
    public ResponseEntity<List<CreatorSearchResponse>> getViewedCreators(@AuthenticationPrincipal UserId userId) {
        return ResponseEntity.ok(historyUseCase.getViewedCreators(userId.value()).stream()
                .map(CreatorSearchResponse::from).toList());
    }

    @GetMapping("/history/queries")
    public ResponseEntity<List<String>> getSearchQueries(@AuthenticationPrincipal UserId userId) {
        return ResponseEntity.ok(historyUseCase.getSearchQueries(userId.value()));
    }
}