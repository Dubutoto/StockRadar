package org.example.stockradar.feature.crawl.repository;



import org.example.stockradar.feature.crawl.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.stockStatus s JOIN FETCH s.price")
    List<Product> findAllWithStockStatusAndPrice();

    List<Product> findAllByProductNameContaining(String s);

    // 키워드별 제품을 찾는 최적화된 쿼리
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE %:keyword1% OR " +
            "LOWER(p.productName) LIKE %:keyword2% OR " +
            "LOWER(p.productName) LIKE %:keyword3% OR " +
            "LOWER(p.productName) LIKE %:keyword4% OR " +
            "LOWER(p.productName) LIKE %:keyword5%")
    List<Product> findKeywordProducts(
            @Param("keyword1") String keyword1,
            @Param("keyword2") String keyword2,
            @Param("keyword3") String keyword3,
            @Param("keyword4") String keyword4,
            @Param("keyword5") String keyword5);

    @Query("SELECT p FROM Product p JOIN FETCH p.stockStatus s JOIN FETCH s.price WHERE p.productId = :productId")
    Product findProductWithStockStatusById(@Param("productId") Long productId);

}
