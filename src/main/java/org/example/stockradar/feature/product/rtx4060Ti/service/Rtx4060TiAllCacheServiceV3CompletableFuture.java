package org.example.stockradar.feature.product.rtx4060Ti.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductCacheDto;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class Rtx4060TiAllCacheServiceV3CompletableFuture {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STATIC_DATA_CACHE_PREFIX = "product:static:";
    private static final String STOCK_STATUS_CACHE_PREFIX = "product:stock:";

    private String createStaticDataCacheKey(Long productId) {
        return STATIC_DATA_CACHE_PREFIX + productId;
    }

    private String createStockStatusCacheKey(Long productId) {
        return STOCK_STATUS_CACHE_PREFIX + productId;
    }

    public List<ProductResponseDto> getRtx4060TiInfo() {
        try {
            log.info("RTX 4060Ti 제품 정보 조회 시작");

            // 1. DB에서 제품 정보 조회
            List<ProductResponseDto> products = productRepository.findKeywordProductDtos(
                    "rtx 4060ti", "rtx 4060 ti", "rtx4060ti", "4060ti", "4060 ti");

            log.info("DB에서 조회된 RTX 4060Ti 제품 수: {}", products.size());

            if (products.isEmpty()) {
                return Collections.emptyList();
            }

            // 제품 ID 목록 추출
            List<Long> productIds = products.stream()
                    .map(ProductResponseDto::getProductId)
                    .collect(Collectors.toList());

            // 각 제품 ID에 대해 비동기로 캐시 조회 요청
            Map<Long, CompletableFuture<ProductResponseDto>> futureMap = new HashMap<>();
            for (Long productId : productIds) {
                futureMap.put(productId, CompletableFuture.supplyAsync(() ->
                        getProductFromCache(productId)));
            }

            // 모든 비동기 작업 완료 대기
            CompletableFuture.allOf(futureMap.values().toArray(new CompletableFuture[0])).join();

            // 결과 맵과 캐시 미스 ID 목록 구성
            Map<Long, ProductResponseDto> resultMap = new HashMap<>();
            List<Long> cacheMissIds = new ArrayList<>();

            for (Long productId : productIds) {
                try {
                    ProductResponseDto cachedProduct = futureMap.get(productId).get();
                    if (cachedProduct != null) {
                        resultMap.put(productId, cachedProduct);
                        log.debug("캐시에서 제품 ID {} 정보 조회 성공", productId);
                    } else {
                        cacheMissIds.add(productId);
                        log.debug("캐시에서 제품 ID {} 정보 미스", productId);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.error("제품 ID {} 캐시 조회 중 오류: {}", productId, e.getMessage());
                    cacheMissIds.add(productId);
                }
            }

            // 캐시 미스가 있는 경우 DB 결과에서 가져와서 캐시에 저장 (비동기 처리)
            if (!cacheMissIds.isEmpty()) {
                log.info("캐시 미스 발생한 제품 수: {}, 캐시 저장 진행", cacheMissIds.size());
                List<CompletableFuture<Void>> cachingFutures = new ArrayList<>();

                for (Long productId : cacheMissIds) {
                    Optional<ProductResponseDto> productOpt = products.stream()
                            .filter(p -> p.getProductId().equals(productId))
                            .findFirst();

                    if (productOpt.isPresent()) {
                        ProductResponseDto product = productOpt.get();
                        resultMap.put(productId, product);
                        cachingFutures.add(CompletableFuture.runAsync(() ->
                                cacheProductData(product)));
                    }
                }

                // 모든 캐싱 작업이 완료될 때까지 기다리지 않고 진행
            }

            // 결과 리스트 반환 (ID 순서 유지)
            List<ProductResponseDto> result = new ArrayList<>();
            for (Long productId : productIds) {
                ProductResponseDto product = resultMap.get(productId);
                if (product != null) {
                    result.add(product);
                }
            }

            log.info("최종 반환되는 RTX 4060Ti 제품 수: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("RTX 4060Ti 제품 정보 조회 중 예외 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private ProductResponseDto getProductFromCache(Long productId) {
        try {
            Object staticDataObj = redisTemplate.opsForValue().get(createStaticDataCacheKey(productId));
            Object stockStatusObj = redisTemplate.opsForValue().get(createStockStatusCacheKey(productId));

            // 두 캐시 모두 없는 경우에만 null 반환
            if (staticDataObj == null && stockStatusObj == null) {
                return null;
            }

            ProductCacheDto staticData = convertToProductCacheDto(staticDataObj);
            ProductCacheDto stockStatus = convertToProductCacheDto(stockStatusObj);

            ProductResponseDto.ProductResponseDtoBuilder builder = ProductResponseDto.builder()
                    .productId(productId);

            // 정적 데이터 설정 (있는 경우에만)
            if (staticData != null) {
                builder.productName(staticData.getProductName())
                        .productUrl(staticData.getProductUrl())
                        .price(staticData.getPrice());
            }

            // 재고 상태 설정 (있는 경우에만)
            if (stockStatus != null) {
                builder.availability(stockStatus.getAvailability());
                // 재고 상태의 가격이 있으면 우선 적용 (더 최신 정보)
                if (stockStatus.getPrice() != null) {
                    builder.price(stockStatus.getPrice());
                }
                builder.lastUpdated(stockStatus.getLastUpdated());
            }

            return builder.build();
        } catch (Exception e) {
            log.error("제품 ID {} 캐시 조회 중 예외 발생: {}", productId, e.getMessage());
            return null;
        }
    }

    private ProductCacheDto convertToProductCacheDto(Object cacheObj) {
        if (cacheObj == null) return null;

        if (cacheObj instanceof ProductCacheDto) {
            return (ProductCacheDto) cacheObj;
        } else if (cacheObj instanceof Map) {
            try {
                Map<String, Object> map = (Map<String, Object>) cacheObj;
                LocalDateTime lastUpdated = null;
                Object lastUpdatedObj = map.get("lastUpdated");

                if (lastUpdatedObj instanceof String) {
                    lastUpdated = LocalDateTime.parse((String) lastUpdatedObj);
                } else if (lastUpdatedObj instanceof LocalDateTime) {
                    lastUpdated = (LocalDateTime) lastUpdatedObj;
                }

                return ProductCacheDto.builder()
                        .productId(map.get("productId") instanceof Number ?
                                ((Number) map.get("productId")).longValue() : null)
                        .productName((String) map.get("productName"))
                        .productUrl((String) map.get("productUrl"))
                        .price(map.get("price") instanceof Number ?
                                ((Number) map.get("price")).longValue() : null)
                        .availability(map.get("availability") instanceof Number ?
                                ((Number) map.get("availability")).intValue() : null)
                        .lastUpdated(lastUpdated)
                        .build();
            } catch (Exception e) {
                log.error("캐시 데이터 변환 중 오류: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    private void cacheProductData(ProductResponseDto product) {
        try {
            Long productId = product.getProductId();
            if (productId == null) {
                log.warn("캐싱 실패: 제품 ID가 null입니다");
                return;
            }

            // 1. 정적 데이터 캐싱 (7일)
            if (product.getProductName() != null || product.getProductUrl() != null) {
                ProductCacheDto staticDataDto = ProductCacheDto.builder()
                        .productId(productId)
                        .productName(product.getProductName())
                        .productUrl(product.getProductUrl())
                        .price(product.getPrice()) // 가격도 정적 데이터에 포함
                        .build();

                redisTemplate.opsForValue().set(
                        createStaticDataCacheKey(productId),
                        staticDataDto,
                        Duration.ofDays(7)
                );
                log.debug("제품 ID {} 정적 데이터 캐싱 완료", productId);
            }

            // 2. 재고 상태 캐싱 (2분)
            if (product.getAvailability() != null || product.getPrice() != null) {
                ProductCacheDto stockStatusDto = ProductCacheDto.builder()
                        .productId(productId)
                        .availability(product.getAvailability())
                        .price(product.getPrice())
                        .lastUpdated(product.getLastUpdated() != null ?
                                product.getLastUpdated() : LocalDateTime.now())
                        .build();

                redisTemplate.opsForValue().set(
                        createStockStatusCacheKey(productId),
                        stockStatusDto,
                        Duration.ofMinutes(2)
                );
                log.debug("제품 ID {} 재고 상태 캐싱 완료", productId);
            }
        } catch (Exception e) {
            log.error("제품 데이터 캐싱 중 예외 발생: {}", e.getMessage());
        }
    }
}
