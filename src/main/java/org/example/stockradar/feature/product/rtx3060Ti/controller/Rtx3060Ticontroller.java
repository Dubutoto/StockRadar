package org.example.stockradar.feature.product.rtx3060Ti.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.service.ProductStockService;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.specific.ProductException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("product")
@RequiredArgsConstructor
@Slf4j
public class Rtx3060Ticontroller {

    private final ProductStockService productStockService;


    @GetMapping("gpu/rtx3060Ti")
    public String rtx3060Ti() {return "product/gpu/rtx3060Ti";}

    //시나리오 1
    @GetMapping("api/gpu/rtx3060Ti")
    public ResponseEntity<?> gpuRtx3060TiApi() {
        log.info("RTX 3060Ti API 요청");

        try {
            // 모든 제품 정보 가져오기
            List<ProductResponseDto> allProducts = productStockService.getProductsWithStockAndPrice();

            // RTX 3060Ti 제품만 필터링
            List<ProductResponseDto> rtx3060TiProducts = allProducts.stream()
                    .filter(product -> product.getProductName() != null &&
                            (product.getProductName().toLowerCase().contains("rtx 3060ti") ||
                                    product.getProductName().toLowerCase().contains("rtx 3060 ti") ||
                                    product.getProductName().toLowerCase().contains("rtx3060ti") ||
                                    product.getProductName().toLowerCase().contains("rtx3060 ti") ||
                                    product.getProductName().toLowerCase().contains("rtx-3060ti") ||
                                    product.getProductName().toLowerCase().contains("rtx-3060 ti") ||
                                    product.getProductName().toLowerCase().contains("rtx 3060-ti") ||
                                    product.getProductName().toLowerCase().contains("rtx3060-ti") ||
                                    product.getProductName().toLowerCase().contains("3060ti") ||
                                    product.getProductName().toLowerCase().contains("3060 ti")
                            ))
                    .collect(Collectors.toList());

            if (rtx3060TiProducts.isEmpty()) {
                log.warn("RTX 3060Ti 제품을 찾을 수 없습니다.");
                ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            log.info("RTX 3060Ti 제품 {} 개를 찾았습니다.", rtx3060TiProducts.size());

            // 성공 응답 DTO 생성
            ProductResponseDto responseDto = ProductResponseDto.builder()
                    .status(200)
                    .message("RTX 3060Ti 제품 조회 성공")
                    .data(rtx3060TiProducts) // ProductResponseDto에 data 필드 추가 필요
                    .build();

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            log.error("RTX 3060Ti 제품 조회 중 오류 발생: {}", e.getMessage(), e);
            ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return null;
    }
}
