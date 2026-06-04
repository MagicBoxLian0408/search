package kr.magicbox.search.adapter.in.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.magicbox.search.adapter.in.security.properties.TrustedIpProperties;
import kr.magicbox.search.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class UserInfoExtractFilter extends OncePerRequestFilter {

    private final TrustedIpProperties trustedIpProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();

        if (!trustedIpProperties.getIps().contains(clientIp)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userIdHeader = request.getHeader("X-User-Id");
        if (!isValidUserId(userIdHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserId userId = UserId.of(Long.valueOf(userIdHeader));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null));

        filterChain.doFilter(request, response);
    }

    private boolean isValidUserId(String value) {
        try {
            return Long.parseLong(value) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
