package org.example.stockradar.feature.crawl.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.notification.entity.InterestProduct;
import org.example.stockradar.feature.notification.repository.InterestProductRepository;
import org.example.stockradar.feature.notification.service.NotificationService;
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
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductStockScheduler {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final InterestProductRepository interestProductRepository;
    private final NotificationService notificationService;

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
            return;
        }


        try {
            // 모든 제품의 정보 조회
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products == null || products.isEmpty()) {
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

                    }
                } catch (Exception e) {
                }
            }


        } catch (Exception e) {
        } finally {
            isStaticCachingRunning.set(false);
        }
    }

    // 매 5분마다 실행 - 재고 상태 갱신
    @Scheduled(cron = "0 */5 * * * *")
    public void cacheProductStockStatus() {
        // 이미 실행 중인지 확인
        if (!isRunning.compareAndSet(false, true)) {
            CrawlException.throwCustomException(ErrorCode.CONCURRENT_SCHEDULER_CONFLICT);
            return;
        }


        try {
            // 모든 제품의 재고 상태를 조회
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products == null || products.isEmpty()) {
                CrawlException.throwCustomException(ErrorCode.DATA_FETCH_TIMEOUT);
            }

            int updatedCount = 0;

            for (Product product : products) {
                try {
                    if (product.getProductId() != null && product.getStockStatus() != null) {
                        String stockCacheKey = createStockStatusCacheKey(product.getProductId());

                        // 재고 상태 변경 감지 및 알림 전송, 최신 재고 상태를 반환받음
                        Integer availability = checkAndSendStockChangeNotification(product);

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

                    }
                } catch (Exception e) {
                }
            }


        } catch (Exception e) {
            CrawlException.throwCustomException(ErrorCode.SCHEDULER_EXECUTION_FAILED);
        } finally {
            isRunning.set(false);
        }
    }

    /**
     * 주어진 제품의 DB에 저장된 마지막 알림 전송 상태와 현재 재고 상태를 비교하여,
     * 변경된 경우 알림 이벤트를 생성 및 전송하고, DB의 lastNotifiedAvailability를 업데이트합니다.
     * @param product 대상 제품
     * @return 현재 재고 상태 (0: 재고 없음, 1: 재고 있음)
     */
    private Integer checkAndSendStockChangeNotification(Product product) {
        // DB에 저장된 마지막 알림 전송 시의 재고 상태
        Integer oldAvailability = product.getStockStatus().getLastNotifiedAvailability();
        // DB의 현재 재고 상태 (예: 0: 재고 없음, 1: 재고 있음)
        Integer newAvailability = product.getStockStatus().getAvailability();

        // 최초 알림 전송 전(초기 로딩)인 경우, 알림 전송 없이 상태만 업데이트
        if (oldAvailability == null) {
            product.getStockStatus().setLastNotifiedAvailability(newAvailability);
            productRepository.save(product);
            log.info("제품 ID {} 최초 알림 상태 설정: {}", product.getProductId(), newAvailability);
        }
        // 이전 알림 상태와 현재 상태가 다르면 알림 전송
        else if (!oldAvailability.equals(newAvailability)) {
            String oldStatus = (oldAvailability == 1) ? "재고 있음" : "재고 없음";
            String newStatus = (newAvailability == 1) ? "재고 있음" : "재고 없음";
            log.info("제품 ID {} 재고 상태 변경 감지: {} -> {}", product.getProductId(), oldStatus, newStatus);

            // 해당 제품에 관심을 등록한 모든 사용자에게 unified 알림 전송 처리
            List<InterestProduct> interestProducts = interestProductRepository.findByProduct_ProductId(product.getProductId());
            if (interestProducts != null && !interestProducts.isEmpty()) {
                for (InterestProduct interestProduct : interestProducts) {
                    // 각 관심 상품 등록 정보에서 회원의 식별정보(이메일)를 가져옴
                    String memberId = interestProduct.getMember().getMemberId();
                    Long interestProductId = interestProduct.getId();
                    // unified sendNotificationEvent 호출
                    // messageContent는 내부에서 채널별로 생성되므로 빈 문자열로 전달합니다.
                    notificationService.sendNotificationEvent(
                            memberId,
                            interestProductId,
                            "",           // messageContent: 내부에서 생성하도록 빈 값 전달
                            newStatus,    // 재고 상태를 텍스트("재고 있음"/"재고 없음")로 전달
                            "stockChange",
                            product.getProductName(),
                            product.getProductUrl()
                    );
                }
            } else {
                log.info("제품 ID {} 에 대해 관심 상품 등록된 사용자가 없습니다.", product.getProductId());
            }

            // 마지막 알림 전송 상태 업데이트
            product.getStockStatus().setLastNotifiedAvailability(newAvailability);
            productRepository.save(product);
        }

        return newAvailability;
    }


}
