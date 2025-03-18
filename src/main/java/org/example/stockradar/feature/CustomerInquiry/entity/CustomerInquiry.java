package org.example.stockradar.feature.CustomerInquiry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.customerInquiryprocessing.entity.CustomerInquiryProcessiong;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long inquiryId;

    @Column(nullable = false)
    private String inquiryCategory;

    @Column(nullable = false)
    private String inquiryTitle;

    @Column(nullable = false)
    private String inquiryContent;

    @Column(nullable = false)
    private Integer inquiryStatus;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    private String inquiryUrl;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    // 1:1 관계 설정 (수정됨)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "customerInquiry")
    @ToString.Exclude // 순환 참조 방지
    private CustomerInquiryProcessiong customerInquiryProcessiong;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_category_id")
    @ToString.Exclude // 순환 참조 방지
    private InquiryCategory inquiryCategoryId;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
