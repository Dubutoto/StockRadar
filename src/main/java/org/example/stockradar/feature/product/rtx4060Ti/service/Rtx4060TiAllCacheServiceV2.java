package org.example.stockradar.feature.product.rtx4060Ti.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.feature.product.dto.ProductCacheDto;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.ProductException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class Rtx4060TiAllCacheServiceV2 {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 복합 캐시 키 접두사 (재고 상태와 정적 데이터 통합)
    private static final String PRODUCT_CACHE_PREFIX = "product:combined:";

    /**
     * 복합 캐시 키 생성
     */
    private String createCacheKey(Long productId) {
        return PRODUCT_CACHE_PREFIX + productId;
    }

    /**
     * 여러 제품의 정보를 한 번에 조회 (벌크 캐시 조회)
     */
    public Map<Long, ProductCacheDto> getProductsData(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        log.info("캐시 조회 시작: {} 개 제품 ID 조회 요청", productIds.size());

        // 캐시 키 목록 생성
        List<String> cacheKeys = productIds.stream()
                .map(this::createCacheKey)
                .collect(Collectors.toList());

        // Redis mget 명령어로 한 번에 조회
        List<Object> cachedValues = redisTemplate.opsForValue().multiGet(cacheKeys);
        Map<Long, ProductCacheDto> result = new HashMap<>();

        // 캐시 미스 목록
        List<Long> cacheMissIds = new ArrayList<>();
        int cacheHitCount = 0;
        int cacheMissCount = 0;

        // 캐시 결과 처리
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Object value = cachedValues.get(i);

            if (value != null) {
                // 캐시 히트 로깅
                log.debug("캐시 히트 - 제품 ID: {}, 캐시 타입: {}", productId, value.getClass().getSimpleName());
                cacheHitCount++;

                // 캐시 히트 - Map인 경우 변환
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    try {
                        ProductCacheDto dto = ProductCacheDto.builder()
                                .productId(productId)
                                .productName((String) map.get("productName"))
                                .productUrl((String) map.get("productUrl"))
                                .price(map.get("price") instanceof Number ?
                                        ((Number) map.get("price")).longValue() : null)
                                .availability(map.get("availability") instanceof Number ?
                                        ((Number) map.get("availability")).intValue() : null)
                                .lastUpdated(LocalDateTime.now())
                                .build();
                        result.put(productId, dto);

                        // 재고 상태 로깅
                        log.debug("제품 ID: {}, 캐시에서 조회된 재고 상태: {}",
                                productId, dto.getAvailability());
                    } catch (Exception e) {
                        log.error("캐시 데이터 변환 중 오류: 제품 ID: {}, 오류: {}", productId, e.getMessage());
                        cacheMissIds.add(productId);
                        cacheMissCount++;
                    }
                } else if (value instanceof ProductCacheDto) {
                    ProductCacheDto dto = (ProductCacheDto) value;
                    result.put(productId, dto);

                    // 재고 상태 로깅
                    log.debug("제품 ID: {}, 캐시에서 조회된 재고 상태: {}",
                            productId, dto.getAvailability());
                } else {
                    log.warn("캐시에서 알 수 없는 타입 반환: 제품 ID: {}, 타입: {}",
                            productId, value.getClass().getName());
                    cacheMissIds.add(productId);
                    cacheMissCount++;
                }
            } else {
                // 캐시 미스
                log.debug("캐시 미스 - 제품 ID: {}", productId);
                cacheMissIds.add(productId);
                cacheMissCount++;
            }
        }

        log.info("캐시 조회 결과: 총 {}개 중 {}개 히트, {}개 미스",
                productIds.size(), cacheHitCount, cacheMissCount);

        // 캐시 미스가 있는 경우 DB에서 조회
        if (!cacheMissIds.isEmpty()) {
            log.info("캐시 미스 처리 시작: {}개 제품 ID DB 조회", cacheMissIds.size());

            int dbFetchSuccessCount = 0;

            // DB에서 한 번에 조회
            for (Long productId : cacheMissIds) {
                try {
                    Product product = productRepository.findProductWithStockStatusById(productId);
                    if (product != null && product.getProductName() != null &&
                            product.getProductUrl() != null && product.getStockStatus() != null) {

                        // 복합 데이터 객체 생성
                        ProductCacheDto cacheDto = ProductCacheDto.builder()
                                .productId(product.getProductId())
                                .productName(product.getProductName())
                                .productUrl(product.getProductUrl())
                                .price(product.getStockStatus().getPrice().getPrice())
                                .availability(product.getStockStatus().getAvailability())
                                .lastUpdated(product.getStockStatus().getLastUpdated())
                                .build();

                        // 캐시에 저장
                        redisTemplate.opsForValue().set(
                                createCacheKey(productId),
                                cacheDto,
                                Duration.ofMinutes(6)
                        );

                        // 결과에 추가
                        result.put(productId, cacheDto);
                        dbFetchSuccessCount++;

                        log.debug("DB에서 조회 성공 - 제품 ID: {}, 재고 상태: {}",
                                productId, cacheDto.getAvailability());
                    } else {
                        log.warn("DB에서 제품 정보 조회 실패 - 제품 ID: {}, 제품 또는 필수 정보 누락", productId);
                    }
                } catch (Exception e) {
                    log.error("제품 ID {}의 DB 조회 중 오류 발생: {}", productId, e.getMessage());
                }
            }

            log.info("DB 조회 결과: 총 {}개 중 {}개 성공", cacheMissIds.size(), dbFetchSuccessCount);
        }

        return result;
    }


    /**
     * 모든 제품 정보를 ProductResponseDto 형태로 반환 (벌크 캐시 조회 활용)
     */
    public List<ProductResponseDto> getAllProducts() {
        try {
            // DB에서 모든 제품 조회
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products.isEmpty()) {
                log.warn("제품을 찾을 수 없습니다.");
                return Collections.emptyList();
            }

            // 제품 ID 목록 추출
            List<Long> productIds = products.stream()
                    .filter(product -> product.getProductId() != null)
                    .map(Product::getProductId)
                    .collect(Collectors.toList());

            // 벌크 캐시 조회 수행
            Map<Long, ProductCacheDto> productDataMap = getProductsData(productIds);

            // 응답 DTO 구성 - 조건부 로직 단순화
            return products.stream()
                    .filter(product -> product.getProductId() != null)
                    .map(product -> {
                        Long productId = product.getProductId();
                        ProductCacheDto cacheDto = productDataMap.get(productId);

                        // 캐시 데이터가 있으면 사용, 없으면 DB 데이터 사용
                        if (cacheDto != null) {
                            return ProductResponseDto.builder()
                                    .productId(productId)
                                    .productName(cacheDto.getProductName())
                                    .productUrl(cacheDto.getProductUrl())
                                    .price(cacheDto.getPrice())
                                    .availability(cacheDto.getAvailability())
                                    .lastUpdated(cacheDto.getLastUpdated())
                                    .build();
                        } else {
                            // 캐시에 없는 경우 (거의 발생하지 않음)
                            return ProductResponseDto.builder()
                                    .productId(productId)
                                    .productName(product.getProductName())
                                    .productUrl(product.getProductUrl())
                                    .price(product.getStockStatus() != null ?
                                            product.getStockStatus().getPrice().getPrice() : null)
                                    .availability(product.getStockStatus() != null ?
                                            product.getStockStatus().getAvailability() : null)
                                    .lastUpdated(product.getStockStatus() != null ?
                                            product.getStockStatus().getLastUpdated() : LocalDateTime.now())
                                    .build();
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("제품 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
