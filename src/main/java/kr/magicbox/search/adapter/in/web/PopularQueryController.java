package kr.magicbox.search.adapter.in.web;

import kr.magicbox.search.adapter.in.web.dto.response.CreatorSearchResponse;
import kr.magicbox.search.adapter.in.web.dto.response.GeneralGoodsSearchResponse;
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
    public ResponseEntity<List<CreatorSearchResponse>> getPopularCreators() {
        return ResponseEntity.ok(
                popularQueryUseCase.getPopularCreators().stream()
                        .map(CreatorSearchResponse::from)
                        .toList()
        );
    }

    @GetMapping("/releases")
    public ResponseEntity<List<ReleaseSearchResponse>> getPopularReleases() {
        return ResponseEntity.ok(
                popularQueryUseCase.getPopularReleases().stream()
                        .map(ReleaseSearchResponse::from)
                        .toList()
        );
    }

    @GetMapping("/general-goods")
    public ResponseEntity<List<GeneralGoodsSearchResponse>> getPopularGeneralGoods() {
        return ResponseEntity.ok(
                popularQueryUseCase.getPopularGeneralGoods().stream()
                        .map(GeneralGoodsSearchResponse::from)
                        .toList()
        );
    }
}
