package org.example.stockradar.feature.notification.repository;

import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.notification.entity.InterestProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Hyun7en
 */

public interface InterestProductRepository extends JpaRepository<InterestProduct, Long> {
    boolean existsByMemberAndProduct(Member member, Product product);

}
