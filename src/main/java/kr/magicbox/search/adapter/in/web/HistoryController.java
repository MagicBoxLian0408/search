package kr.magicbox.search.adapter.in.web;

import kr.magicbox.search.adapter.in.web.dto.response.CreatorSearchResponse;
import kr.magicbox.search.application.port.in.HistoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryUseCase historyUseCase;

    @PostMapping("/creators/{creatorId}")
    public ResponseEntity<Void> recordViewedCreator(
            @RequestAttribute Long userId,
            @PathVariable Long creatorId
    ) {
        historyUseCase.recordViewedCreator(userId, creatorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/creators")
    public ResponseEntity<List<CreatorSearchResponse>> getViewedCreators(
            @RequestAttribute Long userId
    ) {
        return ResponseEntity.ok(
                historyUseCase.getViewedCreators(userId).stream()
                        .map(CreatorSearchResponse::from)
                        .toList()
        );
    }

    @GetMapping("/queries")
    public ResponseEntity<List<String>> getSearchQueries(
            @RequestAttribute Long userId
    ) {
        return ResponseEntity.ok(historyUseCase.getSearchQueries(userId));
    }
}
