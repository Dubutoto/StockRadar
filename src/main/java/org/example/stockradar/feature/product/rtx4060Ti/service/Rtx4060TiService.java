package org.example.stockradar.feature.product.rtx4060Ti.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.ProductException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class Rtx4060TiService {


    private final ProductRepository productRepository;
    //시나리오2
    public List<ProductResponseDto> getRtx4060TiInfo() {
        try {
            // 데이터베이스에서 직접 RTX 4060Ti 제품 필터링
            List<Product> products = productRepository.findKeywordProducts(
                    "rtx 4060ti",
                    "rtx 4060 ti",
                    "rtx4060ti",
                    "4060ti",
                    "4060 ti");

            if (products.isEmpty()) {
                ProductException.throwCustomException(ErrorCode.STOCK_INFO_NOT_FOUND);
            }

            log.info("총 {} 개의 RTX 4060Ti 제품 정보를 조회했습니다.", products.size());

            // Product 엔티티를 ProductResponseDto로 변환
            return products.stream()
                    .map(product -> ProductResponseDto.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .availability(product.getStockStatus().getAvailability())
                            .price(product.getStockStatus().getPrice().getPrice())
                            .lastUpdated(product.getStockStatus().getLastUpdated())
                            .productUrl(product.getProductUrl())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("RTX 4060Ti 제품 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList(); // null 대신 빈 리스트 반환
        }
    }
}
