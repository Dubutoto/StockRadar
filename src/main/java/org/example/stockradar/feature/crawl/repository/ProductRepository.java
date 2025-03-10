package org.example.stockradar.feature.crawl.repository;



import org.example.stockradar.feature.crawl.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.stockStatus s JOIN FETCH s.price")
    List<Product> findAllWithStockStatusAndPrice();

    List<Product> findAllByProductNameContaining(String s);
}
