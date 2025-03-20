package org.example.stockradar.feature.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.crawl.entity.Product;
import org.example.stockradar.feature.crawl.repository.ProductRepository;
import org.example.stockradar.feature.notification.dto.InterestProductRequestDto;
import org.example.stockradar.feature.notification.dto.InterestProductResponseDto;
import org.example.stockradar.feature.notification.entity.InterestProduct;
import org.example.stockradar.feature.notification.repository.InterestProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hyun7en
 */

//관심 상품 관련 로직 service
@Service
@RequiredArgsConstructor
public class InterestProductService {

    private final ProductRepository productRepository;
    private final InterestProductRepository interestProductRepository;

    //관심상품 추가
    @Transactional(rollbackFor = Exception.class)
    public Long registerInterestProduct(InterestProductRequestDto request, Member member) {
        // productId로 상품 정보 조회
        Product product = productRepository.findProductWithStockStatusById(request.getProductId());

        // 이미 등록된 관심 상품이 있는지 확인
        boolean exists = interestProductRepository.existsByMemberAndProduct(member, product);
        if (exists) {
            // 이미 등록된 경우, 별도의 예외를 던지거나 그냥 등록되지 않았다는 메시지를 반환할 수 있음
            throw new IllegalStateException("이미 관심 상품으로 등록되어 있습니다.");
        }

        // InterestProduct 엔터티 생성
        InterestProduct interestProduct = InterestProduct.builder()
                .member(member)
                .product(product)
                .build();

        // 관심 상품 저장
        InterestProduct saved = interestProductRepository.save(interestProduct);

        // 저장된 관심 상품의 ID 반환
        return saved.getId();
    }

    //관심상품 조회
    @Transactional(readOnly = true)
    public Page<InterestProductResponseDto> getInterestProductsByMemberId(String memberId, Pageable pageable) {
        return interestProductRepository.findByMember_MemberId(memberId, pageable)
                .map(interestProduct -> InterestProductResponseDto.builder()
                        .productId(String.valueOf(interestProduct.getProduct().getProductId()))
                        .productName(interestProduct.getProduct().getProductName())
                        .category(interestProduct.getProduct().getCategory().getCategoryName())
                        .productUrl(interestProduct.getProduct().getProductUrl())
                        .availability(interestProduct.getProduct().getStockStatus().getAvailability())
                        .build());
    }

    //관심상품 삭제
    @Transactional(rollbackFor = Exception.class)
    public Long deleteInterestProduct(Long productId, String memberId) {
        InterestProduct interestProduct = interestProductRepository
                .findByMember_MemberIdAndProduct_ProductId(memberId, productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관심 상품이 존재하지 않습니다."));
        Long interestProductId = interestProduct.getId();
        interestProductRepository.delete(interestProduct);
        return interestProductId;
    }

}
