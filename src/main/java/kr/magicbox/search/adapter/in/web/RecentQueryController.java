package kr.magicbox.search.adapter.in.web;

import kr.magicbox.search.adapter.in.web.dto.response.CreatorSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.GeneralGoodsSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.ReleaseSearchResponse;
import kr.magicbox.search.application.port.in.RecentQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recent")
@RequiredArgsConstructor
public class RecentQueryController {

    private final RecentQueryUseCase recentQueryUseCase;

    @GetMapping("/creators")
    public ResponseEntity<List<CreatorSearchResponse>> getRecentCreators() {
        return ResponseEntity.ok(
                recentQueryUseCase.getRecentCreators().stream()
                        .map(CreatorSearchResponse::from)
                        .toList()
        );
    }

    @GetMapping("/releases")
    public ResponseEntity<List<ReleaseSearchResponse>> getRecentReleases() {
        return ResponseEntity.ok(
                recentQueryUseCase.getRecentReleases().stream()
                        .map(ReleaseSearchResponse::from)
                        .toList()
        );
    }

    @GetMapping("/general-goods")
    public ResponseEntity<List<GeneralGoodsSearchResponse>> getRecentGeneralGoods() {
        return ResponseEntity.ok(
                recentQueryUseCase.getRecentGeneralGoods().stream()
                        .map(GeneralGoodsSearchResponse::from)
                        .toList()
        );
    }
}
