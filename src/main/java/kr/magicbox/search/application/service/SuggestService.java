package kr.magicbox.search.application.service;

import kr.magicbox.search.application.port.in.SuggestUseCase;
import kr.magicbox.search.application.port.out.CreatorIndexPort;
import kr.magicbox.search.application.port.out.GeneralGoodsIndexPort;
import kr.magicbox.search.application.port.out.ReleaseIndexPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SuggestService implements SuggestUseCase {

    private final CreatorIndexPort creatorIndexPort;
    private final ReleaseIndexPort releaseIndexPort;
    private final GeneralGoodsIndexPort generalGoodsIndexPort;

    @Override
    public Mono<List<String>> suggest(String keyword) {
        return Mono.zip(
                creatorIndexPort.suggest(keyword),
                releaseIndexPort.suggest(keyword),
                generalGoodsIndexPort.suggest(keyword)
        ).map(tuple -> Stream.of(tuple.getT1(), tuple.getT2(), tuple.getT3())
                .flatMap(List::stream)
                .distinct()
                .limit(10)
                .toList());
    }
}
