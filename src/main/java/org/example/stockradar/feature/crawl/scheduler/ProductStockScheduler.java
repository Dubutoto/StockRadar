package org.example.stockradar.feature.crawl.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductCacheDto;

import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.CrawlException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductStockScheduler {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 정적 데이터와 재고 상태를 위한 별도의 캐시 키 프리픽스
    private static final String STATIC_DATA_CACHE_PREFIX = "product:static:";
    private static final String STOCK_STATUS_CACHE_PREFIX = "product:stock:";

    // 동시 실행 방지를 위한 플래그
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isStaticCachingRunning = new AtomicBoolean(false);

    // 정적 데이터 캐시 키 생성
    private String createStaticDataCacheKey(Long productId) {
        return STATIC_DATA_CACHE_PREFIX + productId;
    }

    // 재고 상태 캐시 키 생성
    private String createStockStatusCacheKey(Long productId) {
        return STOCK_STATUS_CACHE_PREFIX + productId;
    }

    // 서버 시작 시 실행 - 데이터 캐싱
    @PostConstruct
    public void initStaticDataCaching() {
        cacheProductStaticData();
        cacheProductStockStatus();
    }

    // 1주일마다 실행 - 정적 데이터 갱신 (매주 같은 시간)
    @Scheduled(cron = "0 0 0 * * 0") // 매주 일요일 자정에 실행
    public void weeklyStaticDataCaching() {
        cacheProductStaticData();
    }

    // 정적 데이터 캐싱 메서드
    public void cacheProductStaticData() {
        // 이미 실행 중인지 확인
        if (!isStaticCachingRunning.compareAndSet(false, true)) {
            log.warn("정적 데이터 캐싱 작업이 이미 실행 중입니다. 이번 실행은 건너뜁니다.");
            return;
        }

        log.info("상품 정적 데이터 캐싱 스케줄러 시작: {}", LocalDateTime.now().format(formatter));

        try {
            // 모든 제품의 정보 조회
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products == null || products.isEmpty()) {
                log.warn("캐싱할 제품 정보가 없습니다.");
                return;
            }

            int cachedCount = 0;

            for (Product product : products) {
                try {
                    if (product.getProductId() != null) {
                        // 정적 데이터만 캐싱
                        String cacheKey = createStaticDataCacheKey(product.getProductId());

                        ProductCacheDto staticDataDto = ProductCacheDto.builder()
                                .productId(product.getProductId())
                                .productName(product.getProductName())
                                .productUrl(product.getProductUrl())
                                .price(product.getStockStatus() != null ?
                                        product.getStockStatus().getPrice().getPrice() : null)
                                .build();

                        // TTL 1주일로 설정
                        redisTemplate.opsForValue().set(cacheKey, staticDataDto, Duration.ofDays(7));
                        cachedCount++;

                        // log.debug("제품 ID {} 정적 데이터 캐싱 완료: {}", product.getProductId(), product.getProductName());
                    }
                } catch (Exception e) {
                    // log.error("상품 정적 데이터 처리 중 오류 발생: {}, 오류: {}", product.getProductName(), e.getMessage());
                }
            }

            // log.info("정적 데이터 캐싱 완료: 총 {} 개 제품 중 {} 개 캐싱됨", products.size(), cachedCount);

        } catch (Exception e) {
            // log.error("정적 데이터 캐싱 중 예외 발생: {}", e.getMessage(), e);
        } finally {
            isStaticCachingRunning.set(false);
            log.info("상품 정적 데이터 캐싱 종료: {}", LocalDateTime.now().format(formatter));
        }
    }

    // 매 5분마다 실행 - 재고 상태 갱신
    @Scheduled(cron = "0 */5 * * * *")
    public void cacheProductStockStatus() {
        // 이미 실행 중인지 확인
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("이전 스케줄러 작업이 아직 실행 중입니다. 이번 실행은 건너뜁니다.");
            CrawlException.throwCustomException(ErrorCode.CONCURRENT_SCHEDULER_CONFLICT);
            return;
        }

        log.info("상품 재고 상태 갱신 스케줄러 시작: {}", LocalDateTime.now().format(formatter));

        try {
            // 모든 제품의 재고 상태를 조회
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products == null || products.isEmpty()) {
                log.warn("갱신할 제품 정보가 없습니다.");
                CrawlException.throwCustomException(ErrorCode.DATA_FETCH_TIMEOUT);
            }

            int updatedCount = 0;

            for (Product product : products) {
                try {
                    if (product.getProductId() != null && product.getStockStatus() != null) {
                        String stockCacheKey = createStockStatusCacheKey(product.getProductId());
                        Integer availability = product.getStockStatus().getAvailability();
                        Long price = product.getStockStatus().getPrice() != null ?
                                product.getStockStatus().getPrice().getPrice() : null;

                        // 재고 상태만 포함하는 객체 생성
                        ProductCacheDto stockStatusDto = ProductCacheDto.builder()
                                .availability(availability)
                                .price(price)
                                .lastUpdated(LocalDateTime.now())
                                .build();

                        // TTL 6분으로 설정 (재고 상태는 짧게 유지)
                        redisTemplate.opsForValue().set(stockCacheKey, stockStatusDto, Duration.ofMinutes(6));
                        updatedCount++;

                        // log.debug("제품 ID {} 재고 상태 갱신 완료: {}", product.getProductId(), availability);
                    }
                } catch (Exception e) {
                    log.error("상품 재고 상태 처리 중 오류 발생: {}, 오류: {}", product.getProductName(), e.getMessage());
                }
            }

            log.info("재고 상태 갱신 완료: 총 {} 개 제품 중 {} 개 갱신됨", products.size(), updatedCount);

        } catch (Exception e) {
            log.error("스케줄러 실행 중 예외 발생: {}", e.getMessage(), e);
            CrawlException.throwCustomException(ErrorCode.SCHEDULER_EXECUTION_FAILED);
        } finally {
            isRunning.set(false);
            log.info("상품 재고 상태 갱신 스케줄러 종료: {}", LocalDateTime.now().format(formatter));
        }
    }
}
