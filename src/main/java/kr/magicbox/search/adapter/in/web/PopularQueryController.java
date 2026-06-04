package kr.magicbox.search.adapter.in.web;

import kr.magicbox.search.adapter.in.web.dto.response.CreatorSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.GeneralGoodsSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.PageResponse;
import kr.magicbox.search.adapter.in.web.dto.response.ReleaseSearchResponse;
import kr.magicbox.search.application.port.in.PopularQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/popular")
@RequiredArgsConstructor
public class PopularQueryController {

    private final PopularQueryUseCase popularQueryUseCase;

    @GetMapping("/creators")
    public ResponseEntity<PageResponse<CreatorSearchResponse>> getPopularCreators() {
        List<CreatorSearchResponse> content = popularQueryUseCase.getPopularCreators().stream()
                .map(CreatorSearchResponse::from)
                .toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    @GetMapping("/releases")
    public ResponseEntity<PageResponse<ReleaseSearchResponse>> getPopularReleases() {
        List<ReleaseSearchResponse> content = popularQueryUseCase.getPopularReleases().stream()
                .map(ReleaseSearchResponse::from)
                .toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }

    @GetMapping("/general-goods")
    public ResponseEntity<PageResponse<GeneralGoodsSearchResponse>> getPopularGeneralGoods() {
        List<GeneralGoodsSearchResponse> content = popularQueryUseCase.getPopularGeneralGoods().stream()
                .map(GeneralGoodsSearchResponse::from)
                .toList();
        return ResponseEntity.ok(PageResponse.of(content, 0, content.size()));
    }
}
