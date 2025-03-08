package org.example.stockradar.feature.crawl.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.service.ProductStockService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductStockScheduler {

    private final ProductStockService productStockService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 매 1분마다 실행 (cron = "초 분 시 일 월 요일")
    @Scheduled(cron = "0 */1 * * * *")
    public void checkProductStockStatus() {
        log.info("상품 재고 상태 조회 스케줄러 시작: {}", LocalDateTime.now().format(formatter));

        List<Product> products = productStockService.getProductsWithStockAndPrice();

        for (Product product : products) {
            log.info("상품명: {}, 재고상태: {}, 가격: {}, 마지막 업데이트: {}",
                    product.getProductName(),
                    product.getStockStatus().getAvailability(),
                    product.getStockStatus().getPrice().getPrice(),
                    product.getStockStatus().getLastUpdated().format(formatter));
        }

        log.info("상품 재고 상태 조회 스케줄러 종료: {}", LocalDateTime.now().format(formatter));
    }
}

