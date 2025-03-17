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
public class Rtx4060TiLikeV2Service {


    private final ProductRepository productRepository;
    //시나리오2 버전2
    public List<ProductResponseDto> getRtx4060TiInfo() {
        try {
            // 직접 DTO로 매핑하는 쿼리 사용
            List<ProductResponseDto> products = productRepository.findKeywordProductDtos(
                    "rtx 4060ti",
                    "rtx 4060 ti",
                    "rtx4060ti",
                    "4060ti",
                    "4060 ti");

            if (products.isEmpty()) {
                ProductException.throwCustomException(ErrorCode.STOCK_INFO_NOT_FOUND);
            }

            log.info("총 {} 개의 RTX 4060Ti 제품 정보를 조회했습니다.", products.size());

            // 변환 불필요, 바로 반환
            return products;

        } catch (Exception e) {
            log.error("RTX 4060Ti 제품 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
