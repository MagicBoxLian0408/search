package kr.magicbox.search.adapter.out.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisCacheAdapter implements SearchCachePort {

    private static final String POPULAR_CREATORS_KEY = "search:popular:creators";
    private static final String POPULAR_RELEASES_KEY = "search:popular:releases";
    private static final String POPULAR_GENERAL_GOODS_KEY = "search:popular:general-goods";
    private static final String RECENT_CREATORS_KEY = "search:recent:creators";
    private static final String RECENT_RELEASES_KEY = "search:recent:releases";
    private static final String RECENT_GENERAL_GOODS_KEY = "search:recent:general-goods";
    private static final String VIEWED_CREATORS_KEY_PREFIX = "search:history:creators:";
    private static final String SEARCH_QUERIES_KEY_PREFIX = "search:history:queries:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    // ===== Cache Aside: 인기 목록 =====

    @Override
    public Optional<List<CreatorDocument>> getPopularCreators() {
        return getList(POPULAR_CREATORS_KEY, new TypeReference<>() {});
    }

    @Override
    public void setPopularCreators(List<CreatorDocument> creators) {
        setWithTtl(POPULAR_CREATORS_KEY, creators, cacheProperties.getPopularTtlSeconds());
    }

    @Override
    public Optional<List<ReleaseDocument>> getPopularReleases() {
        return getList(POPULAR_RELEASES_KEY, new TypeReference<>() {});
    }

    @Override
    public void setPopularReleases(List<ReleaseDocument> releases) {
        setWithTtl(POPULAR_RELEASES_KEY, releases, cacheProperties.getPopularTtlSeconds());
    }

    @Override
    public Optional<List<GeneralGoodsDocument>> getPopularGeneralGoods() {
        return getList(POPULAR_GENERAL_GOODS_KEY, new TypeReference<>() {});
    }

    @Override
    public void setPopularGeneralGoods(List<GeneralGoodsDocument> goods) {
        setWithTtl(POPULAR_GENERAL_GOODS_KEY, goods, cacheProperties.getPopularTtlSeconds());
    }

    // ===== Write Through: 최신 목록 =====

    @Override
    public void addRecentCreator(CreatorDocument document) {
        addToList(RECENT_CREATORS_KEY, document, cacheProperties.getRecentListSize());
    }

    @Override
    public List<CreatorDocument> getRecentCreators() {
        return getListItems(RECENT_CREATORS_KEY, new TypeReference<>() {});
    }

    @Override
    public void addRecentRelease(ReleaseDocument document) {
        addToList(RECENT_RELEASES_KEY, document, cacheProperties.getRecentListSize());
    }

    @Override
    public List<ReleaseDocument> getRecentReleases() {
        return getListItems(RECENT_RELEASES_KEY, new TypeReference<>() {});
    }

    @Override
    public void addRecentGeneralGoods(GeneralGoodsDocument document) {
        addToList(RECENT_GENERAL_GOODS_KEY, document, cacheProperties.getRecentListSize());
    }

    @Override
    public List<GeneralGoodsDocument> getRecentGeneralGoods() {
        return getListItems(RECENT_GENERAL_GOODS_KEY, new TypeReference<>() {});
    }

    // ===== Write Through: 개인화 이력 =====

    @Override
    public void addViewedCreator(Long userId, CreatorDocument document) {
        String key = VIEWED_CREATORS_KEY_PREFIX + userId;
        addToList(key, document, cacheProperties.getHistoryListSize());
    }

    @Override
    public List<CreatorDocument> getViewedCreators(Long userId) {
        return getListItems(VIEWED_CREATORS_KEY_PREFIX + userId, new TypeReference<>() {});
    }

    @Override
    public void addSearchQuery(Long userId, String query) {
        String key = SEARCH_QUERIES_KEY_PREFIX + userId;
        try {
            redisTemplate.opsForList().leftPush(key, query);
            redisTemplate.opsForList().trim(key, 0, cacheProperties.getQueryHistorySize() - 1);
        } catch (Exception e) {
            log.error("[Cache] 검색어 저장 실패. userId={}, query={}", userId, query, e);
        }
    }

    @Override
    public List<String> getSearchQueries(Long userId) {
        try {
            List<String> result = redisTemplate.opsForList()
                    .range(SEARCH_QUERIES_KEY_PREFIX + userId, 0, -1);
            return result != null ? result : List.of();
        } catch (Exception e) {
            log.error("[Cache] 검색어 조회 실패. userId={}", userId, e);
            return List.of();
        }
    }

    // ===== 내부 헬퍼 =====

    private <T> Optional<List<T>> getList(String key, TypeReference<List<T>> typeRef) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(value, typeRef));
        } catch (Exception e) {
            log.error("[Cache] 캐시 조회 실패. key={}", key, e);
            return Optional.empty();
        }
    }

    private void setWithTtl(String key, Object value, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value),
                    Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.error("[Cache] 캐시 저장 실패. key={}", key, e);
        }
    }

    private <T> void addToList(String key, T item, int maxSize) {
        try {
            redisTemplate.opsForList().leftPush(key, objectMapper.writeValueAsString(item));
            redisTemplate.opsForList().trim(key, 0, maxSize - 1);
        } catch (Exception e) {
            log.error("[Cache] 리스트 추가 실패. key={}", key, e);
        }
    }

    private <T> List<T> getListItems(String key, TypeReference<T> typeRef) {
        try {
            List<String> raw = redisTemplate.opsForList().range(key, 0, -1);
            if (raw == null || raw.isEmpty()) return List.of();
            return raw.stream()
                    .map(s -> {
                        try {
                            return objectMapper.<T>readValue(s, typeRef);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(item -> item != null)
                    .toList();
        } catch (Exception e) {
            log.error("[Cache] 리스트 조회 실패. key={}", key, e);
            return List.of();
        }
    }
}
