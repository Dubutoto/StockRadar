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

        // 캐시 키 목록 생성
        List<String> cacheKeys = productIds.stream()
                .map(this::createCacheKey)
                .collect(Collectors.toList());

        // Redis mget 명령어로 한 번에 조회
        List<Object> cachedValues = redisTemplate.opsForValue().multiGet(cacheKeys);
        Map<Long, ProductCacheDto> result = new HashMap<>();

        // 캐시 미스 목록
        List<Long> cacheMissIds = new ArrayList<>();

        // 캐시 결과 처리
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Object value = cachedValues.get(i);

            if (value != null) {
                // 캐시 히트
                result.put(productId, (ProductCacheDto) value);
            } else {
                // 캐시 미스
                cacheMissIds.add(productId);
            }
        }

        // 캐시 미스가 있는 경우 DB에서 조회
        if (!cacheMissIds.isEmpty()) {
            log.debug("캐시 미스 - 제품 ID: {}, DB에서 조회합니다.", cacheMissIds);

            // DB에서 한 번에 조회 (실제 구현은 DB가 벌크 조회를 지원하는지에 따라 달라질 수 있음)
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

                        // 재고 상태는 6분, 정적 데이터는 7일이므로 더 짧은 6분으로 설정
                        redisTemplate.opsForValue().set(
                                createCacheKey(productId),
                                cacheDto,
                                Duration.ofMinutes(6)
                        );

                        // 결과에 추가
                        result.put(productId, cacheDto);
                    }
                } catch (Exception e) {
                    log.error("제품 ID {}의 데이터 조회 중 오류 발생: {}", productId, e.getMessage());
                }
            }
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
