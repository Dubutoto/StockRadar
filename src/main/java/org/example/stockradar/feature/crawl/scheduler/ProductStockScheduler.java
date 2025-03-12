package org.example.stockradar.feature.crawl.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.CrawlException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductStockScheduler {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String STOCK_CACHE_PREFIX = "product:stock:";

    // 동시 실행 방지를 위한 플래그
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    // 재고 상태 캐시 키 생성
    private String createStockCacheKey(Long productId) {
        return STOCK_CACHE_PREFIX + productId;
    }

    // 재고 상태 캐싱_시나리오3
    private void cacheProductStock(Long productId, Integer availability) {
        String cacheKey = createStockCacheKey(productId);
        redisTemplate.opsForValue().set(cacheKey, availability, Duration.ofMinutes(6));
        log.debug("제품 ID {} 재고 상태 캐싱 완료: {}", productId, availability);
    }

    // 매 5분마다 실행 (cron = "초 분 시 일 월 요일")
    @Scheduled(cron = "0 */5 * * * *")
    public void cacheProductStockStatus() {
        // 이미 실행 중인지 확인
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("이전 스케줄러 작업이 아직 실행 중입니다. 이번 실행은 건너뜁니다.");
            CrawlException.throwCustomException(ErrorCode.CONCURRENT_SCHEDULER_CONFLICT);
        }

        log.info("상품 재고 상태 캐싱 스케줄러 시작: {}", LocalDateTime.now().format(formatter));

        try {
            // 모든 제품의 재고 상태를 조회
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products == null || products.isEmpty()) {
                log.warn("캐싱할 제품 정보가 없습니다.");
                CrawlException.throwCustomException(ErrorCode.DATA_FETCH_TIMEOUT);
            }

            int cachedCount = 0;

            for (Product product : products) {
                try {
                    if (product.getProductId() != null && product.getStockStatus() != null) {
                        // 재고 상태만 캐싱 (TTL 6분 - 스케줄러 주기보다 약간 길게)
                        String cacheKey = createStockCacheKey(product.getProductId());
                        Integer availability = product.getStockStatus().getAvailability();

                        redisTemplate.opsForValue().set(cacheKey, availability, Duration.ofMinutes(6));
                        cachedCount++;

                        // 제품 정보 로깅
                        log.info("상품명: {}, 재고상태: {}, 가격: {}, 마지막 업데이트: {}",
                                product.getProductName(),
                                availability,
                                product.getStockStatus().getPrice().getPrice(),
                                product.getStockStatus().getLastUpdated().format(formatter));

                        log.debug("제품 ID {} 재고 상태 캐싱 완료: {}", product.getProductId(), availability);
                    }
                } catch (Exception e) {
                    // 개별 상품 처리 실패 시 로그만 남기고 다음 상품으로 진행
                    log.error("상품 처리 중 오류 발생: {}, 오류: {}", product.getProductName(), e.getMessage());
                }
            }

            log.info("재고 상태 캐싱 완료: 총 {} 개 제품 중 {} 개 캐싱됨", products.size(), cachedCount);

        } catch (Exception e) {
            // 기타 예외는 SCHEDULER_EXECUTION_FAILED로 변환
            log.error("스케줄러 실행 중 예외 발생: {}", e.getMessage(), e);
            CrawlException.throwCustomException(ErrorCode.SCHEDULER_EXECUTION_FAILED);
        } finally {
            // 실행 상태 플래그 초기화
            isRunning.set(false);
            log.info("상품 재고 상태 캐싱 스케줄러 종료: {}", LocalDateTime.now().format(formatter));
        }
    }
}
