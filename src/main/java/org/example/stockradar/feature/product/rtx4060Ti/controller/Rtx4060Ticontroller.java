package org.example.stockradar.feature.product.rtx4060Ti.controller;

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
public class Rtx4060Ticontroller {

    private final ProductStockService productStockService;


    @GetMapping("gpu/rtx4060Ti")
    public String rtx4060Ti() {return "product/gpu/rtx4060Ti";}

//    //시나리오 1
//    @GetMapping("api/gpu/rtx4060Ti")
//    public ResponseEntity<?> gpuRtx4060TiApi() {
//        log.info("RTX 4060Ti API 요청");
//
//        try {
//            // 모든 제품 정보 가져오기
//            List<ProductResponseDto> allProducts = productStockService.getProductsWithStockAndPrice();
//
//            // RTX 4060Ti 제품만 필터링
//            List<ProductResponseDto> rtx4060TiProducts = allProducts.stream()
//                    .filter(product -> product.getProductName() != null &&
//                            (product.getProductName().toLowerCase().contains("rtx 4060ti") ||
//                                    product.getProductName().toLowerCase().contains("rtx 4060 ti") ||
//                                    product.getProductName().toLowerCase().contains("rtx4060ti") ||
//                                    product.getProductName().toLowerCase().contains("rtx4060 ti") ||
//                                    product.getProductName().toLowerCase().contains("rtx-4060ti") ||
//                                    product.getProductName().toLowerCase().contains("rtx-4060 ti") ||
//                                    product.getProductName().toLowerCase().contains("rtx 4060-ti") ||
//                                    product.getProductName().toLowerCase().contains("rtx4060-ti") ||
//                                    product.getProductName().toLowerCase().contains("4060ti") ||
//                                    product.getProductName().toLowerCase().contains("4060 ti")
//                            ))
//                    .collect(Collectors.toList());
//
//            if (rtx4060TiProducts.isEmpty()) {
//                log.warn("RTX 4060Ti 제품을 찾을 수 없습니다.");
//                ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
//            }
//
//            log.info("RTX 4060Ti 제품 {} 개를 찾았습니다.", rtx4060TiProducts.size());
//
//            // 성공 응답 DTO 생성
//            ProductResponseDto responseDto = ProductResponseDto.builder()
//                    .status(200)
//                    .message("RTX 4060Ti 제품 조회 성공")
//                    .data(rtx4060TiProducts) // ProductResponseDto에 data 필드 추가 필요
//                    .build();
//
//            return ResponseEntity.ok(responseDto);
//
//        } catch (Exception e) {
//            log.error("RTX 4060Ti 제품 조회 중 오류 발생: {}", e.getMessage(), e);
//            ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//        return null;
//    }

    //시나리오2
    @GetMapping("api/gpu/rtx4060Ti")
    public ResponseEntity<?> getRtx4060Ti() {
        log.info("Rtx4060Ti요청");

        try {
            List<ProductResponseDto> rtx4060TiProducts = productStockService.getRtx4060TiInfo();
            if(rtx4060TiProducts.isEmpty()) {
                log.warn("RTX 4060Ti 제품을 찾을 수 없습니다.");
                ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            log.info("RTX 4060Ti 제품 {} 개를 찾았습니다.", rtx4060TiProducts.size());

            // 성공 응답 DTO 생성
            ProductResponseDto responseDto = ProductResponseDto.builder()
                    .status(200)
                    .message("RTX 3060Ti 제품 조회 성공")
                    .data(rtx4060TiProducts)
                    .build();

            return ResponseEntity.ok(responseDto);
        }catch (Exception e) {
            log.error("RTX 4060Ti 제품 조회 중 오류 발생: {}", e.getMessage(), e);
            ProductException.throwCustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return null;
    }
}
