package kr.magicbox.search.application.port.in;

import reactor.core.publisher.Mono;

import java.util.List;

public interface SuggestUseCase {
    Mono<List<String>> suggest(String keyword);
}
