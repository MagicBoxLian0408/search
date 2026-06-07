package kr.magicbox.search.adapter.out.cache.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private final long popularTtlMinutes;
    private final long recentTtlMinutes;
    private final long popularQueryTtlHours;
    private final int historyMaxSize;
}
