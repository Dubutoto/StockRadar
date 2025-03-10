package org.example.stockradar.feature.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductStockServiceWithCache {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 재고 상태 캐시 키 생성
    private String createStockCacheKey(Long productId) {
        return "product:stock:" + productId;
    }


    // 캐시에서 재고 상태 조회
    public Integer getProductStockFromCache(Long productId) {
        String cacheKey = createStockCacheKey(productId);
        Integer availability = (Integer) redisTemplate.opsForValue().get(cacheKey);

        if (availability != null) {
            log.info("캐시에서 제품 ID {} 재고 상태 조회: {}", productId, availability);
            return availability;
        }

        return null;
    }

    //  제품 목록 조회 (재고 상태는 캐시에서 조회)
    public List<ProductResponseDto> getAllProductsWithCachedStock() {
        // DB에서 모든 정보 조회
        List<Product> products = productRepository.findAll();

        // 제품 정보를 DTO로 변환하면서 재고 상태는 캐시에서 조회
        return products.stream()
                .map(product -> {
                    // 캐시에서 재고 상태 조회
                    Integer cachedAvailability = getProductStockFromCache(product.getProductId());

                    // 캐시에 없으면 DB에서 조회하고 캐싱
                    if (cachedAvailability == null) {
                        log.info("캐시 미스: 제품 ID {} 재고 상태를 DB에서 조회", product.getProductId());
                        cachedAvailability = product.getStockStatus().getAvailability();

                        // DB에서 조회한 데이터를 캐시에 저장
                        String cacheKey = createStockCacheKey(product.getProductId());
                        redisTemplate.opsForValue().set(cacheKey, cachedAvailability, Duration.ofMinutes(1));
                        log.info("제품 ID {} 재고 상태 캐싱 완료: {}", product.getProductId(), cachedAvailability);
                    }

                    // DTO 생성
                    return ProductResponseDto.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .availability(cachedAvailability)
                            .price(product.getStockStatus().getPrice().getPrice())
                            .lastUpdated(product.getStockStatus().getLastUpdated())
                            .redirectUrl(product.getProductUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }



}
