package kr.magicbox.search.adapter.in.security.configuration;

import kr.magicbox.search.adapter.in.security.filter.UserInfoExtractFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> Mono.just(authentication);
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authenticationManager(reactiveAuthenticationManager())
                .addFilterAt(new UserInfoExtractFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(auth -> auth.anyExchange().permitAll())
                .build();
    }
}
