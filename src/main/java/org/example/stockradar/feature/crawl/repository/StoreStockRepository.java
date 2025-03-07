package org.example.stockradar.feature.crawl.repository;

import org.example.stockradar.feature.crawl.entity.StoreStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreStockRepository extends JpaRepository<StoreStock, Long> {
    // 필요하면 findByStoreNameAndProductId 등 추가
}
