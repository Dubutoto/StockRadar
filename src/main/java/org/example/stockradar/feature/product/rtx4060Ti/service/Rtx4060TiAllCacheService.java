package org.example.stockradar.feature.product.rtx4060Ti.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.feature.product.dto.ProductStaticCacheDto;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.ProductException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class Rtx4060TiAllCacheService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STOCK_CACHE_PREFIX = "product:stock:";
    private static final String STATIC_CACHE_PREFIX = "product:static:";


    /**
     * 제품 ID로 재고 상태 조회 (캐시 우선, 캐시 미스 시 DB 조회 후 캐싱)
     */
    public Integer getProductStockStatus(Long productId) {
        String cacheKey = STOCK_CACHE_PREFIX + productId;

        // 캐시에서 먼저 조회
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            log.debug("재고 상태 캐시 히트 - 제품 ID: {}", productId);
            return (Integer) cachedValue;
        }

        log.debug("재고 상태 캐시 미스 - 제품 ID: {}, DB에서 조회합니다.", productId);

        // 캐시 미스 시 DB에서 조회
        try {
            Product product = productRepository.findProductWithStockStatusById(productId);
            if (product != null && product.getStockStatus() != null) {
                Integer availability = product.getStockStatus().getAvailability();

                // 재조회한 데이터를 캐시에 저장 (TTL 6분)
                redisTemplate.opsForValue().set(cacheKey, availability, Duration.ofMinutes(6));
                log.debug("제품 ID {}의 재고 상태를 캐시에 저장: {}", productId, availability);

                return availability;
            } else {
                log.warn("제품 ID {}에 대한 재고 정보를 찾을 수 없습니다.", productId);
                return null;
            }
        } catch (Exception e) {
            log.error("제품 ID {}의 재고 상태 조회 중 오류 발생: {}", productId, e.getMessage());
            ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return null;
    }

    /**
     * 제품 ID로 정적 데이터 조회 (캐시 우선, 캐시 미스 시 DB 조회 후 캐싱)
     */
    public ProductStaticCacheDto getProductStaticData(Long productId) {
        String cacheKey = STATIC_CACHE_PREFIX + productId;

        // 캐시에서 먼저 조회
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            log.debug("정적 데이터 캐시 히트 - 제품 ID: {}", productId);
            return (ProductStaticCacheDto) cachedValue;
        }

        log.debug("정적 데이터 캐시 미스 - 제품 ID: {}, DB에서 조회합니다.", productId);

        // 캐시 미스 시 DB에서 조회
        try {
            Product product = productRepository.findProductWithStockStatusById(productId);
            if (product != null && product.getProductName() != null && product.getProductUrl() != null
                    && product.getStockStatus() != null && product.getStockStatus().getPrice() != null) {

                ProductStaticCacheDto staticData = ProductStaticCacheDto.builder()
                        .productId(product.getProductId())
                        .productName(product.getProductName())
                        .productUrl(product.getProductUrl())
                        .price(product.getStockStatus().getPrice().getPrice())
                        .build();

                // 재조회한 데이터를 캐시에 저장 (TTL 7일)
                redisTemplate.opsForValue().set(cacheKey, staticData, Duration.ofDays(7));
                log.debug("제품 ID {}의 정적 데이터를 캐시에 저장: {}", productId, product.getProductName());

                return staticData;
            } else {
                log.warn("제품 ID {}에 대한 정적 데이터를 찾을 수 없습니다.", productId);
                return null;
            }
        } catch (Exception e) {
            log.error("제품 ID {}의 정적 데이터 조회 중 오류 발생: {}", productId, e.getMessage());
            ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return null;
    }



    /**
     * 모든 제품 정보를 ProductResponseDto 형태로 반환
     */
    public List<ProductResponseDto> getAllProducts() {
        try {
            // DB에서 모든 제품 조회
            List<Product> products = productRepository.findAllWithStockStatusAndPrice();

            if (products.isEmpty()) {
                log.warn("제품을 찾을 수 없습니다.");
                return Collections.emptyList();
            }

            // 각 제품에 대해 캐시를 활용하여 정보 구성
            return products.stream()
                    .filter(product -> product.getProductId() != null)
                    .map(product -> {
                        Long productId = product.getProductId();

                        // 캐시에서 재고 상태 조회 (캐시 미스 시 DB 조회 후 캐싱)
                        Integer availability = getProductStockStatus(productId);

                        // 캐시에서 정적 데이터 조회 (캐시 미스 시 DB 조회 후 캐싱)
                        ProductStaticCacheDto staticData = getProductStaticData(productId);

                        // 응답 DTO 구성
                        return ProductResponseDto.builder()
                                .productId(productId)
                                .productName(staticData != null ? staticData.getProductName() : product.getProductName())
                                .productUrl(staticData != null ? staticData.getProductUrl() : product.getProductUrl())
                                .price(staticData != null ? staticData.getPrice() :
                                        (product.getStockStatus() != null && product.getStockStatus().getPrice() != null ?
                                                product.getStockStatus().getPrice().getPrice() : null))
                                .availability(availability)
                                .lastUpdated(product.getStockStatus() != null ?
                                        product.getStockStatus().getLastUpdated() : LocalDateTime.now())
                                .build();
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("제품 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
