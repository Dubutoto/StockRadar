package org.example.stockradar.feature.CustomerInquiry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

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

    @CreationTimestamp
    private LocalDate createdAt;

    private String inquiryUrl;






    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;


}
