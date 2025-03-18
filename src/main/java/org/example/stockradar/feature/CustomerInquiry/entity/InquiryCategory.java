package org.example.stockradar.feature.CustomerInquiry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.customerInquiryprocessing.entity.CustomerInquiryProcessiong;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long inquiryCategoryId;

    // 1:1 관계 설정 (수정됨)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "InquiryCategory")
    @ToString.Exclude // 순환 참조 방지
    private CustomerInquiry customerInquiry;


}
