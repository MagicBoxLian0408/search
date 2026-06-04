package kr.magicbox.search.adapter.out.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "search.cache")
public class CacheProperties {
    private final long popularTtlSeconds;
    private final int popularSize;
    private final int recentListSize;
    private final int historyListSize;
    private final int queryHistorySize;
}
