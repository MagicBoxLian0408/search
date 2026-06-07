package kr.magicbox.search.adapter.in.security.filter;

import kr.magicbox.search.domain.vo.UserId;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class UserInfoExtractFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String userIdHeader = request.getHeaders().getFirst("X-User-Id");

        if (!isValidUserId(userIdHeader)) {
            return chain.filter(exchange);
        }

        UserId userId = UserId.of(Long.valueOf(userIdHeader));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private boolean isValidUserId(String userIdHeader) {
        try {
            return Long.parseLong(userIdHeader) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
