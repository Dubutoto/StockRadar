package org.example.stockradar.feature.crawl.repository;

import org.example.stockradar.feature.crawl.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 필요하면 findByName(String name) 등 추가
}
