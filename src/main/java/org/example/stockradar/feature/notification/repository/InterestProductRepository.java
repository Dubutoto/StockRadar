package org.example.stockradar.feature.notification.repository;

import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.notification.dto.InterestProductResponseDto;
import org.example.stockradar.feature.notification.entity.InterestProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Hyun7en
 */

public interface InterestProductRepository extends JpaRepository<InterestProduct, Long> {
    /**
     * 특정 회원(Member)과 상품(Product)에 대해 관심 상품이 등록되어 있는지 확인합니다.
     *
     * @param member  관심 상품을 등록한 회원 엔티티
     * @param product 관심 상품으로 등록된 상품 엔티티
     * @return 해당 회원과 상품에 대해 관심 상품이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByMemberAndProduct(Member member, Product product);

    /**
     * 특정 회원이 등록한 관심 상품 목록을 페이징 처리하여 조회합니다.
     *
     * @param memberId 회원의 식별자 (예: 이메일 또는 고유 ID)
     * @param pageable 페이징 정보(페이지 번호, 페이지 크기, 정렬 정보 등)
     * @return 해당 회원의 관심 상품 목록을 담은 Page 객체
     */
    Page<InterestProduct> findByMember_MemberId(String memberId, Pageable pageable);

    /**
     * 특정 회원이 특정 상품에 대해 등록한 관심 상품 정보를 조회합니다.
     *
     * @param memberId  회원의 식별자
     * @param productId 상품의 식별자
     * @return 조회된 관심 상품 정보를 Optional로 감싸서 반환 (존재하지 않을 경우 empty)
     */
    Optional<InterestProduct> findByMember_MemberIdAndProduct_ProductId(String memberId, Long productId);

    /**
     * 특정 상품에 대해 등록된 모든 관심 상품 정보를 조회합니다.
     *
     * @param productId 상품의 식별자
     * @return 해당 상품에 대해 등록된 관심 상품 목록
     */
    List<InterestProduct> findByProduct_ProductId(Long productId);
}
