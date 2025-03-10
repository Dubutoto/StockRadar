package org.example.stockradar.product.rxt3050;

import org.example.stockradar.feature.crawl.service.ProductStockService;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.feature.product.rtx3050.controller.Rtx3050Controller;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.example.stockradar.global.exception.ErrorResponse;
import org.example.stockradar.global.exception.specific.ProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class Rtx3050ControllerExceptionTest {

    @Mock
    private ProductStockService productStockService;

    @InjectMocks
    private Rtx3050Controller rtx3050Controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rtx3050Controller)
                .setControllerAdvice(new GlobalExceptionHandler()) // 전역 예외 핸들러 추가
                .build();
    }

    @Test
    @DisplayName("RTX 3050 페이지를 정상적으로 반환한다")
    void rtx3050_ReturnsViewName() throws Exception {
        mockMvc.perform(get("/product/gpu/rtx3050"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/gpu/rtx3050"));
    }

    @Test
    @DisplayName("RTX 3050 제품 목록을 정상적으로 반환한다")
    void gpuRtx3050Api_ReturnsProductList() throws Exception {
        // 목 데이터 생성
        List<ProductResponseDto> mockProducts = createMockRtx3050Products();

        // 서비스 동작 모의
        when(productStockService.getProductsWithStockAndPrice()).thenReturn(mockProducts);

        // API 호출 및 검증
        mockMvc.perform(get("/product/api/gpu/rtx3050"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("RTX 3050 제품 조회 성공"))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    @DisplayName("RTX 3050 제품이 없을 경우 PRODUCT_NOT_FOUND 예외를 발생시킨다")
    void gpuRtx3050Api_WhenNoRtx3050Products_ThrowsProductNotFoundException() throws Exception {
        // RTX 3050이 아닌 다른 제품만 포함된 목 데이터 생성
        List<ProductResponseDto> mockProducts = createMockNonRtx3050Products();

        // 서비스 동작 모의
        when(productStockService.getProductsWithStockAndPrice()).thenReturn(mockProducts);

        // API 호출 및 예외 검증
        mockMvc.perform(get("/product/api/gpu/rtx3050"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_FOUND.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage").value(ErrorCode.PRODUCT_NOT_FOUND.getErrorMessage()));
    }

    @Test
    @DisplayName("서비스 호출 중 예외 발생 시 PRODUCT_NOT_FOUND 예외를 발생시킨다")
    void gpuRtx3050Api_WhenServiceThrowsException_ThrowsProductNotFoundException() throws Exception {
        // 서비스 예외 발생 모의
        when(productStockService.getProductsWithStockAndPrice()).thenThrow(new RuntimeException("서비스 오류"));

        // API 호출 및 예외 검증
        mockMvc.perform(get("/product/api/gpu/rtx3050"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_FOUND.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage").value(ErrorCode.PRODUCT_NOT_FOUND.getErrorMessage()));
    }

    @Test
    @DisplayName("빈 제품 목록 반환 시 PRODUCT_NOT_FOUND 예외를 발생시킨다")
    void gpuRtx3050Api_WhenEmptyProductList_ThrowsProductNotFoundException() throws Exception {
        // 빈 목록 반환 모의
        when(productStockService.getProductsWithStockAndPrice()).thenReturn(Collections.emptyList());

        // API 호출 및 예외 검증
        mockMvc.perform(get("/product/api/gpu/rtx3050"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_FOUND.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage").value(ErrorCode.PRODUCT_NOT_FOUND.getErrorMessage()));
    }

    // RTX 3050 제품 목 데이터 생성 메소드
    private List<ProductResponseDto> createMockRtx3050Products() {
        List<ProductResponseDto> products = new ArrayList<>();

        products.add(ProductResponseDto.builder()
                .productName("ASUS RTX 3050 DUAL OC")
                .availability(5)
                .price(250000L)
                .lastUpdated(LocalDateTime.now())
                .redirectUrl("https://example.com/asus-rtx3050")
                .build());

        products.add(ProductResponseDto.builder()
                .productName("MSI RTX 3050 GAMING X")
                .availability(0)
                .price(270000L)
                .lastUpdated(LocalDateTime.now())
                .redirectUrl("https://example.com/msi-rtx3050")
                .build());

        products.add(ProductResponseDto.builder()
                .productName("GIGABYTE RTX 3050 EAGLE OC")
                .availability(3)
                .price(260000L)
                .lastUpdated(LocalDateTime.now())
                .redirectUrl("https://example.com/gigabyte-rtx3050")
                .build());

        return products;
    }

    // RTX 3050이 아닌 제품 목 데이터 생성 메소드
    private List<ProductResponseDto> createMockNonRtx3050Products() {
        List<ProductResponseDto> products = new ArrayList<>();

        products.add(ProductResponseDto.builder()
                .productName("Intel Core i9-13900K")
                .availability(10)
                .price(650000L)
                .lastUpdated(LocalDateTime.now())
                .redirectUrl("https://example.com/intel-i9-13900k")
                .build());

        products.add(ProductResponseDto.builder()
                .productName("AMD Ryzen 9 7950X")
                .availability(7)
                .price(700000L)
                .lastUpdated(LocalDateTime.now())
                .redirectUrl("https://example.com/amd-ryzen-7950x")
                .build());

        return products;
    }

    // GlobalExceptionHandler 클래스 (테스트용 내부 클래스)
    private static class GlobalExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(CustomException.class)
        public org.springframework.http.ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
            ErrorResponse errorResponse = ErrorResponse.of(
                    ex.getHttpStatus(), ex.getErrorCode(), ex.getErrorMessage(), ex.getHint()
            );
            return new org.springframework.http.ResponseEntity<>(errorResponse,
                    org.springframework.http.HttpStatus.valueOf(ex.getHttpStatus()));
        }
    }
}