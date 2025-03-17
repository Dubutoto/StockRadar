package org.example.stockradar.feature.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductALLServiceWithCache {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 제품 전체 데이터 캐시 키 생성
    private String createProductCacheKey(Long productId) {
        return "product:data:" + productId;
    }

    // 캐시에서 제품 데이터 조회
    public ProductResponseDto getProductFromCache(Long productId) {
        String cacheKey = createProductCacheKey(productId);

        // Redis Hash에서 데이터 조회
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            String productName = (String) redisTemplate.opsForHash().get(cacheKey, "productName");
            Integer availability = (Integer) redisTemplate.opsForHash().get(cacheKey, "availability");
            String productUrl = (String) redisTemplate.opsForHash().get(cacheKey, "productUrl");
            Long price = (Long) redisTemplate.opsForHash().get(cacheKey, "price");

            // 캐시에서 조회 성공 로그
            log.info("캐시에서 제품 ID {} 데이터 조회 성공", productId);

            // DTO 생성 및 반환
            return ProductResponseDto.builder()
                    .productId(productId)
                    .productName(productName)
                    .availability(availability)
                    .price(price)
                    .redirectUrl(productUrl)
                    .build();
        }

        return null;
    }

    // 모든 제품 조회 (캐시 우선)
    public List<ProductResponseDto> getAllProducts() {
        // DB에서 모든 제품 기본 정보 조회
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> result = new ArrayList<>();

        for (Product product : products) {
            // 캐시에서 제품 데이터 조회
            ProductResponseDto cachedProduct = getProductFromCache(product.getProductId());

            if (cachedProduct != null) {
                // 캐시에서 조회된 데이터 사용
                result.add(cachedProduct);
            } else {
                // 캐시 미스: DB에서 조회한 데이터 사용
                log.info("캐시 미스: 제품 ID {} 데이터를 DB에서 조회", product.getProductId());

                ProductResponseDto dto = ProductResponseDto.builder()
                        .productId(product.getProductId())
                        .productName(product.getProductName())
                        .availability(product.getStockStatus().getAvailability())
                        .price(product.getStockStatus().getPrice().getPrice())
                        .lastUpdated(product.getStockStatus().getLastUpdated())
                        .redirectUrl(product.getProductUrl())
                        .build();

                // DB에서 조회한 데이터를 Redis에 캐싱
                String cacheKey = createProductCacheKey(product.getProductId());
                redisTemplate.opsForHash().put(cacheKey, "productName", product.getProductName());
                redisTemplate.opsForHash().put(cacheKey, "availability", product.getStockStatus().getAvailability());
                redisTemplate.opsForHash().put(cacheKey, "productUrl", product.getProductUrl());
                redisTemplate.opsForHash().put(cacheKey, "price", product.getStockStatus().getPrice().getPrice());
                // 캐시 만료 시간 설정 (1분)
                redisTemplate.expire(cacheKey, java.time.Duration.ofMinutes(1));

                log.info("제품 ID {} 데이터 캐싱 완료", product.getProductId());

                result.add(dto);

            }
        }

        return result;
    }

    // 특정 키워드로 제품 필터링 (캐시 우선)
    public List<ProductResponseDto> getProductsByKeyword(String keyword) {
        List<ProductResponseDto> allProducts = getAllProducts();

        // 키워드로 필터링
        return allProducts.stream()
                .filter(product -> product.getProductName() != null &&
                        product.getProductName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
