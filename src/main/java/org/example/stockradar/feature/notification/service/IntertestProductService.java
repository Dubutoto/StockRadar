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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class IntertestProductService {

    private final ProductRepository productRepository;
    private final InterestProductRepository interestProductRepository;

    //관심상품 추가
    @Transactional
    public Long registerInterestProduct(InterestProductRequestDto request, Member member) {
        // productId로 상품 정보 조회
        Product product = productRepository.findProductWithStockStatusById(request.getProductId());

        // 이미 등록된 관심 상품이 있는지 확인
        boolean exists = interestProductRepository.existsByMemberAndProduct(member, product);
        if (exists) {
            // 이미 등록된 경우, 별도의 예외를 던지거나 그냥 등록되지 않았다는 메시지를 반환할 수 있음
            throw new IllegalStateException("이미 관심 상품으로 등록되어 있습니다.");
        }

        // InterestProduct 엔터티 생성 (웹 푸시 알림은 기본 활성화)
        InterestProduct interestProduct = InterestProduct.builder()
                .member(member)
                .product(product)
                .webPushNotification(true)
                .build();

        // 관심 상품 저장
        InterestProduct saved = interestProductRepository.save(interestProduct);

        // 저장된 관심 상품의 ID 반환
        return saved.getId();
    }

    //관심상품 조회
    public Page<InterestProductResponseDto> getInterestProductsByMemberId(String memberId) {

    }

    //관심상품 삭제


}
