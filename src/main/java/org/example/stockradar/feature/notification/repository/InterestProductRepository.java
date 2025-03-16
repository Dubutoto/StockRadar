package org.example.stockradar.feature.notification.repository;

import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.notification.dto.InterestProductResponseDto;
import org.example.stockradar.feature.notification.entity.InterestProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Hyun7en
 */

public interface InterestProductRepository extends JpaRepository<InterestProduct, Long> {
    boolean existsByMemberAndProduct(Member member, Product product);

    Page<InterestProduct> findByMember_MemberId(String memberId, Pageable pageable);

    Optional<InterestProduct> findByMember_MemberIdAndProduct_ProductId(String memberId, Long productId);

}
