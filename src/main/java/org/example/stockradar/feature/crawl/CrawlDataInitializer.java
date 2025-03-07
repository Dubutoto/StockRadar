package org.example.stockradar.feature.crawl;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.entity.StoreStock;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.crawl.repository.StoreStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final StoreStockRepository storeStockRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1) 상품 등록
        Product p1 = new Product();
        p1.setName("RTX 3060 Ti");
        productRepository.save(p1);

        // 2) 해당 상품을 파는 여러 스토어(URL) 등록
        StoreStock ssgStock = new StoreStock();
        ssgStock.setProduct(p1);
        ssgStock.setStoreName("SSG");
        ssgStock.setUrl("https://www.ssg.com/item/itemView.ssg?itemId=1000321766163");
        ssgStock.setInStock(false);
        ssgStock.setPrice(0);
        storeStockRepository.save(ssgStock);

        StoreStock coupangStock = new StoreStock();
        coupangStock.setProduct(p1);
        coupangStock.setStoreName("Coupang");
        coupangStock.setUrl("https://www.coupang.com/vp/products/4534845613?itemId=5481674614");
        coupangStock.setInStock(false);
        coupangStock.setPrice(0);
        storeStockRepository.save(coupangStock);

        StoreStock amazonStock = new StoreStock();
        amazonStock.setProduct(p1);
        amazonStock.setStoreName("Amazon");
        amazonStock.setUrl("https://www.amazon.com/Zotac-Gaming-GeForce-NVIDIA-GDDR6X/dp/B0BN4DKMQC");
        amazonStock.setInStock(false);
        amazonStock.setPrice(0);
        storeStockRepository.save(amazonStock);

        System.out.println("[CrawlDataInitializer] 초기 URL 데이터 삽입 완료!");
    }
}
