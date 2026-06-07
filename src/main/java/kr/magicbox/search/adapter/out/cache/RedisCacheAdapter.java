package kr.magicbox.search.adapter.out.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Range;
import kr.magicbox.search.adapter.out.cache.properties.CacheProperties;
import kr.magicbox.search.adapter.out.elasticsearch.document.CreatorDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.GeneralGoodsDocument;
import kr.magicbox.search.adapter.out.elasticsearch.document.ReleaseDocument;
import kr.magicbox.search.application.port.out.SearchCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheAdapter implements SearchCachePort {

    private static final String POPULAR_CREATORS_KEY = "search:popular:creators";
    private static final String POPULAR_RELEASES_KEY = "search:popular:releases";
    private static final String POPULAR_GENERAL_GOODS_KEY = "search:popular:general-goods";
    private static final String RECENT_CREATORS_KEY = "search:recent:creators";
    private static final String RECENT_RELEASES_KEY = "search:recent:releases";
    private static final String RECENT_GENERAL_GOODS_KEY = "search:recent:general-goods";
    private static final String POPULAR_QUERIES_KEY = "search:popular:queries";
    private static final String HISTORY_CREATORS_KEY_PREFIX = "search:history:creators:";
    private static final String HISTORY_RELEASES_KEY_PREFIX = "search:history:releases:";
    private static final String HISTORY_GENERAL_GOODS_KEY_PREFIX = "search:history:general-goods:";
    private static final String HISTORY_QUERIES_KEY_PREFIX = "search:history:queries:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    // ===== 인기 목록 캐시 =====

    @Override
    public Mono<List<CreatorDocument>> getPopularCreators() {
        return redisTemplate.opsForValue().get(POPULAR_CREATORS_KEY)
                .mapNotNull(json -> deserializeList(json, new TypeReference<List<CreatorDocument>>() {}));
    }

    @Override
    public Mono<Void> setPopularCreators(List<CreatorDocument> list) {
        return serializeAndSet(POPULAR_CREATORS_KEY, list, Duration.ofMinutes(cacheProperties.getPopularTtlMinutes()));
    }

    @Override
    public Mono<List<ReleaseDocument>> getPopularReleases() {
        return redisTemplate.opsForValue().get(POPULAR_RELEASES_KEY)
                .mapNotNull(json -> deserializeList(json, new TypeReference<List<ReleaseDocument>>() {}));
    }

    @Override
    public Mono<Void> setPopularReleases(List<ReleaseDocument> list) {
        return serializeAndSet(POPULAR_RELEASES_KEY, list, Duration.ofMinutes(cacheProperties.getPopularTtlMinutes()));
    }

    @Override
    public Mono<List<GeneralGoodsDocument>> getPopularGeneralGoods() {
        return redisTemplate.opsForValue().get(POPULAR_GENERAL_GOODS_KEY)
                .mapNotNull(json -> deserializeList(json, new TypeReference<List<GeneralGoodsDocument>>() {}));
    }

    @Override
    public Mono<Void> setPopularGeneralGoods(List<GeneralGoodsDocument> list) {
        return serializeAndSet(POPULAR_GENERAL_GOODS_KEY, list, Duration.ofMinutes(cacheProperties.getPopularTtlMinutes()));
    }

    // ===== 최신 목록 캐시 =====

    @Override
    public Mono<List<CreatorDocument>> getRecentCreators() {
        return redisTemplate.opsForValue().get(RECENT_CREATORS_KEY)
                .mapNotNull(json -> deserializeList(json, new TypeReference<List<CreatorDocument>>() {}));
    }

    @Override
    public Mono<Void> setRecentCreators(List<CreatorDocument> list) {
        return serializeAndSet(RECENT_CREATORS_KEY, list, Duration.ofMinutes(cacheProperties.getRecentTtlMinutes()));
    }

    @Override
    public Mono<List<ReleaseDocument>> getRecentReleases() {
        return redisTemplate.opsForValue().get(RECENT_RELEASES_KEY)
                .mapNotNull(json -> deserializeList(json, new TypeReference<List<ReleaseDocument>>() {}));
    }

    @Override
    public Mono<Void> setRecentReleases(List<ReleaseDocument> list) {
        return serializeAndSet(RECENT_RELEASES_KEY, list, Duration.ofMinutes(cacheProperties.getRecentTtlMinutes()));
    }

    @Override
    public Mono<List<GeneralGoodsDocument>> getRecentGeneralGoods() {
        return redisTemplate.opsForValue().get(RECENT_GENERAL_GOODS_KEY)
                .mapNotNull(json -> deserializeList(json, new TypeReference<List<GeneralGoodsDocument>>() {}));
    }

    @Override
    public Mono<Void> setRecentGeneralGoods(List<GeneralGoodsDocument> list) {
        return serializeAndSet(RECENT_GENERAL_GOODS_KEY, list, Duration.ofMinutes(cacheProperties.getRecentTtlMinutes()));
    }

    // ===== 인기 검색어 (ZSet) =====

    @Override
    public Mono<Void> incrementQueryScore(String keyword) {
        return redisTemplate.opsForZSet().incrementScore(POPULAR_QUERIES_KEY, keyword, 1.0)
                .then(redisTemplate.expire(POPULAR_QUERIES_KEY, Duration.ofHours(cacheProperties.getPopularQueryTtlHours())))
                .then();
    }

    @Override
    public Mono<List<String>> getPopularQueries(int size) {
        return redisTemplate.opsForZSet().reverseRange(POPULAR_QUERIES_KEY, Range.closed(0L, (long) (size - 1)))
                .collectList();
    }

    // ===== 최근 본 아이템 =====

    @Override
    public Mono<Void> addViewedCreator(Long userId, CreatorDocument doc) {
        String key = HISTORY_CREATORS_KEY_PREFIX + userId;
        String json = serialize(doc);
        return redisTemplate.opsForList().remove(key, 0, json)
                .then(redisTemplate.opsForList().leftPush(key, json))
                .then(redisTemplate.opsForList().trim(key, 0, cacheProperties.getHistoryMaxSize() - 1))
                .then();
    }

    @Override
    public Mono<List<CreatorDocument>> getViewedCreators(Long userId) {
        String key = HISTORY_CREATORS_KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, -1)
                .mapNotNull(json -> deserialize(json, CreatorDocument.class))
                .collectList();
    }

    @Override
    public Mono<Void> addViewedRelease(Long userId, ReleaseDocument doc) {
        String key = HISTORY_RELEASES_KEY_PREFIX + userId;
        String json = serialize(doc);
        return redisTemplate.opsForList().remove(key, 0, json)
                .then(redisTemplate.opsForList().leftPush(key, json))
                .then(redisTemplate.opsForList().trim(key, 0, cacheProperties.getHistoryMaxSize() - 1))
                .then();
    }

    @Override
    public Mono<List<ReleaseDocument>> getViewedReleases(Long userId) {
        String key = HISTORY_RELEASES_KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, -1)
                .mapNotNull(json -> deserialize(json, ReleaseDocument.class))
                .collectList();
    }

    @Override
    public Mono<Void> addViewedGeneralGoods(Long userId, GeneralGoodsDocument doc) {
        String key = HISTORY_GENERAL_GOODS_KEY_PREFIX + userId;
        String json = serialize(doc);
        return redisTemplate.opsForList().remove(key, 0, json)
                .then(redisTemplate.opsForList().leftPush(key, json))
                .then(redisTemplate.opsForList().trim(key, 0, cacheProperties.getHistoryMaxSize() - 1))
                .then();
    }

    @Override
    public Mono<List<GeneralGoodsDocument>> getViewedGeneralGoods(Long userId) {
        String key = HISTORY_GENERAL_GOODS_KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, -1)
                .mapNotNull(json -> deserialize(json, GeneralGoodsDocument.class))
                .collectList();
    }

    // ===== 최근 검색어 =====

    @Override
    public Mono<Void> addSearchQuery(Long userId, String keyword) {
        String key = HISTORY_QUERIES_KEY_PREFIX + userId;
        return redisTemplate.opsForList().remove(key, 0, keyword)
                .then(redisTemplate.opsForList().leftPush(key, keyword))
                .then(redisTemplate.opsForList().trim(key, 0, cacheProperties.getHistoryMaxSize() - 1))
                .then();
    }

    @Override
    public Mono<List<String>> getSearchQueries(Long userId) {
        String key = HISTORY_QUERIES_KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, -1)
                .collectList();
    }

    // ===== 직렬화 헬퍼 =====

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Redis 직렬화 실패: {}", e.getMessage());
            throw new IllegalStateException("Redis 직렬화 실패", e);
        }
    }

    private <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Redis 역직렬화 실패: {}", e.getMessage());
            return null;
        }
    }

    private <T> List<T> deserializeList(String json, TypeReference<List<T>> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            log.error("Redis 리스트 역직렬화 실패: {}", e.getMessage());
            return null;
        }
    }

    private Mono<Void> serializeAndSet(String key, Object value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            return redisTemplate.opsForValue().set(key, json, ttl).then();
        } catch (Exception e) {
            log.error("Redis 직렬화 실패: {}", e.getMessage());
            return Mono.empty();
        }
    }
}
