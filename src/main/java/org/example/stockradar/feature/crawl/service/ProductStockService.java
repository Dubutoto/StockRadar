package org.example.stockradar.feature.crawl.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductStockService {

    private final ProductRepository productRepository;
    //1.비동기식 조회 처리 비교
    //2.Redis 캐싱후 성능 조회


    public List<Product> getProductsWithStockAndPrice() {
        List<Product> products = productRepository.findAllWithStockStatusAndPrice();
        log.info("총 {} 개의 상품 정보를 조회했습니다.", products.size());
        return products;
    }
}
