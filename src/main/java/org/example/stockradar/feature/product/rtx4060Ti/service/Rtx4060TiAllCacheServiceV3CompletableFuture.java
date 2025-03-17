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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class Rtx4060TiAllCacheServiceV3CompletableFuture {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 정적 데이터와 재고 상태를 위한 별도의 캐시 키 프리픽스
    private static final String STATIC_DATA_CACHE_PREFIX = "product:static:";
    private static final String STOCK_STATUS_CACHE_PREFIX = "product:stock:";

    // 정적 데이터 캐시 키 생성
    private String createStaticDataCacheKey(Long productId) {
        return STATIC_DATA_CACHE_PREFIX + productId;
    }

    // 재고 상태 캐시 키 생성
    private String createStockStatusCacheKey(Long productId) {
        return STOCK_STATUS_CACHE_PREFIX + productId;
    }

    /**
     * RTX 4060 Ti 제품 정보 조회
     * 1. 캐시에서 비동기로 조회
     * 2. 캐시 미스 시 DB에서 조회 후 캐시에 비동기로 저장
     */
    public List<ProductResponseDto> getRtx4060TiInfo() {
        try {
            // 1. DB에서 직접 DTO로 매핑하여 RTX 4060 Ti 제품 정보 조회
            List<ProductResponseDto> products = productRepository.findKeywordProductDtos(
                    "rtx 4060ti",
                    "rtx 4060 ti",
                    "rtx4060ti",
                    "4060ti",
                    "4060 ti");

            if (products.isEmpty()) {
                log.warn("RTX 4060 Ti 제품을 찾을 수 없습니다.");
                return Collections.emptyList();
            }

            log.info("DB에서 RTX 4060 Ti 제품 {}개 조회 완료", products.size());

            // 2. 제품 ID 목록
            List<Long> productIds = products.stream()
                    .map(ProductResponseDto::getProductId)
                    .collect(Collectors.toList());

            // 3. 캐시에서 데이터 조회 - CompletableFuture 활용한 비동기 처리
            Map<Long, CompletableFuture<ProductResponseDto>> futureMap = new HashMap<>();

            // 각 제품 ID에 대해 비동기로 캐시 조회 요청
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
                    ProductResponseDto cachedProduct = futureMap.get(productId).get(); // 결과 가져오기
                    if (cachedProduct != null) {
                        resultMap.put(productId, cachedProduct);
                    } else {
                        cacheMissIds.add(productId);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.error("캐시 조회 결과 처리 중 오류: {}", e.getMessage());
                    cacheMissIds.add(productId); // 오류 발생 시 캐시 미스로 처리
                }
            }

            log.info("캐시 조회 결과: {}개 중 {}개 히트, {}개 미스",
                    productIds.size(), resultMap.size(), cacheMissIds.size());

            // 4. 캐시 미스가 있는 경우 DB 결과에서 가져와서 캐시에 저장 (비동기 처리)
            if (!cacheMissIds.isEmpty()) {
                List<CompletableFuture<Void>> cachingFutures = new ArrayList<>();

                for (Long productId : cacheMissIds) {
                    Optional<ProductResponseDto> productOpt = products.stream()
                            .filter(p -> p.getProductId().equals(productId))
                            .findFirst();

                    if (productOpt.isPresent()) {
                        ProductResponseDto product = productOpt.get();

                        // 결과에 먼저 추가 (동기적으로 처리)
                        resultMap.put(productId, product);

                        // 캐시 저장 작업을 비동기로 수행
                        cachingFutures.add(CompletableFuture.runAsync(() ->
                                cacheProductData(product)));
                    }
                }

                // 선택적: 모든 캐싱 작업이 완료될 때까지 대기
                // 주석 처리하면 캐싱은 백그라운드에서 계속 진행되고 응답은 즉시 반환됨
                // CompletableFuture.allOf(cachingFutures.toArray(new CompletableFuture[0])).join();

                log.info("캐시 미스 처리: {}개 항목에 대한 비동기 캐싱 작업 시작", cachingFutures.size());
            }

            // 5. 결과 리스트 반환 (ID 순서 유지)
            List<ProductResponseDto> result = new ArrayList<>();
            for (Long productId : productIds) {
                ProductResponseDto product = resultMap.get(productId);
                if (product != null) {
                    result.add(product);
                }
            }

            return result;

        } catch (Exception e) {
            log.error("RTX 4060 Ti 제품 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    /**
     * 캐시에서 제품 정보를 조회하는 메서드
     */
    private ProductResponseDto getProductFromCache(Long productId) {
        try {
            // 정적 데이터 캐시 조회
            Object staticDataObj = redisTemplate.opsForValue().get(createStaticDataCacheKey(productId));
            // 재고 상태 캐시 조회
            Object stockStatusObj = redisTemplate.opsForValue().get(createStockStatusCacheKey(productId));

            if (staticDataObj == null && stockStatusObj == null) {
                // 완전 캐시 미스 (둘 다 없음)
                return null;
            }

            // 캐시 데이터 변환
            ProductCacheDto staticData = convertToProductCacheDto(staticDataObj);
            ProductCacheDto stockStatus = convertToProductCacheDto(stockStatusObj);

            // 응답 DTO 구성
            ProductResponseDto.ProductResponseDtoBuilder builder = ProductResponseDto.builder()
                    .productId(productId);

            // 정적 데이터가 있는 경우
            if (staticData != null) {
                builder.productName(staticData.getProductName())
                        .productUrl(staticData.getProductUrl())
                        .price(staticData.getPrice());
            }

            // 재고 상태가 있는 경우 (재고 상태의 정보가 우선)
            if (stockStatus != null) {
                builder.availability(stockStatus.getAvailability())
                        .price(stockStatus.getPrice())
                        .lastUpdated(stockStatus.getLastUpdated());
            }

            return builder.build();
        } catch (Exception e) {
            log.error("캐시에서 제품 ID {} 정보 조회 중 오류 발생: {}", productId, e.getMessage());
            return null;
        }
    }

    /**
     * 캐시 객체를 ProductCacheDto로 변환
     */
    private ProductCacheDto convertToProductCacheDto(Object cacheObj) {
        if (cacheObj == null) {
            return null;
        }

        if (cacheObj instanceof ProductCacheDto) {
            return (ProductCacheDto) cacheObj;
        } else if (cacheObj instanceof Map) {
            try {
                Map<String, Object> map = (Map<String, Object>) cacheObj;

                // LocalDateTime 변환 로직 추가
                LocalDateTime lastUpdated = null;
                Object lastUpdatedObj = map.get("lastUpdated");
                if (lastUpdatedObj instanceof String) {
                    // 문자열로 저장된 날짜를 LocalDateTime으로 변환
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


    /**
     * 제품 정보를 캐시에 저장하는 메서드
     */
    private void cacheProductData(ProductResponseDto product) {
        try {
            Long productId = product.getProductId();

            // 1. 정적 데이터 캐싱 (7일)
            ProductCacheDto staticDataDto = ProductCacheDto.builder()
                    .productId(productId)
                    .productName(product.getProductName())
                    .productUrl(product.getProductUrl())
                    .price(product.getPrice())
                    .build();

            redisTemplate.opsForValue().set(
                    createStaticDataCacheKey(productId),
                    staticDataDto,
                    Duration.ofDays(7)
            );

            // 2. 재고 상태 캐싱 (6분)
            ProductCacheDto stockStatusDto = ProductCacheDto.builder()
                    .availability(product.getAvailability())
                    .price(product.getPrice())
                    .lastUpdated(product.getLastUpdated() != null ?
                            product.getLastUpdated() : LocalDateTime.now())
                    .build();

            redisTemplate.opsForValue().set(
                    createStockStatusCacheKey(productId),
                    stockStatusDto,
                    Duration.ofMinutes(6)
            );

            log.debug("제품 ID {} 캐싱 완료: 정적 데이터(7일), 재고 상태(6분)", productId);
        } catch (Exception e) {
            log.error("제품 정보 캐싱 중 오류 발생: {}", e.getMessage());
        }
    }
}
