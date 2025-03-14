package org.example.stockradar.feature.product.rtx4060Ti.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.ProductException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class Rtx4060TiStockCacheService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String STOCK_CACHE_PREFIX = "product:stock:";

    // 재고 상태 캐시 키 생성
    private String createStockCacheKey(Long productId) {
        return STOCK_CACHE_PREFIX + productId;
    }

    // 시나리오3: 캐시에서 재고 상태를 조회하는 메소드
    public Integer getCachedStockStatus(Long productId) {
        String cacheKey = createStockCacheKey(productId);
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            log.debug("제품 ID {}의 재고 상태 캐시 히트", productId);
            return (Integer) cachedValue;
        }

        log.debug("제품 ID {}의 재고 상태 캐시 미스", productId);

        // 캐시 미스 시 DB에서 값을 가져와 캐싱
        try {
            Product product = productRepository.findProductWithStockStatusById(productId);
            if (product != null && product.getStockStatus() != null) {
                Integer availability = product.getStockStatus().getAvailability();
                // Redis에 값 캐싱 (TTL 6분 설정)
                redisTemplate.opsForValue().set(cacheKey, availability, java.time.Duration.ofMinutes(6));
                log.debug("제품 ID {}의 재고 상태를 캐시에 저장: {}", productId, availability);
                return availability;
            }
        } catch (Exception e) {
            log.error("제품 ID {}의 재고 상태 조회 중 오류 발생: {}", productId, e.getMessage());
        }

        return null;
    }




    // 시나리오3: 키워드로 제품 검색
    public List<ProductResponseDto> getRtx4060TiInfo() {
        try {
            // 데이터베이스에서 직접 RTX 4060Ti 제품 필터링
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products.isEmpty()) {
                ProductException.throwCustomException(ErrorCode.STOCK_INFO_NOT_FOUND);
            }

            log.info("총 {} 개의 RTX 4060Ti 제품 정보를 조회했습니다.", products.size());

            return products.stream()
                    .map(product -> {
                        // 캐시에서 재고 상태 확인 (캐시 미스 시 DB에서 조회하여 캐싱)
                        Integer availability = null;
                        if(product.getProductId() != null) {
                            availability = getCachedStockStatus(product.getProductId());
                        } else if(product.getStockStatus() != null) {
                            // ProductId가 없는 경우 (비정상적인 상황)
                            availability = product.getStockStatus().getAvailability();
                            log.warn("ProductId가 null인 제품 발견: {}", product.getProductName());
                            //예외처리
                            ProductException.throwCustomException(ErrorCode.PRODUCT_ID_NULL);
                        }

                        return ProductResponseDto.builder()
                                .productId(product.getProductId())
                                .productName(product.getProductName())
                                .availability(availability)
                                .price(product.getStockStatus().getPrice().getPrice())
                                .lastUpdated(product.getStockStatus().getLastUpdated())
                                .productUrl(product.getProductUrl())
                                .build();
                    })
                    .collect(Collectors.toList());


        } catch (Exception e) {
            log.error("RTX 4060Ti 제품 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
