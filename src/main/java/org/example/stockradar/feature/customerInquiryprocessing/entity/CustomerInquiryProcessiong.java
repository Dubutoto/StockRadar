package org.example.stockradar.feature.customerInquiryprocessing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInquiryProcessiong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long processingId;

    @Column(nullable = false)
    private String processingTitle;

    @Column(nullable = false)
    private String processingContent;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime finishTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiryId")
    @ToString.Exclude // 순환 참조 방지
    private CustomerInquiry customerInquiry;

    @PrePersist
    protected void onCreate() {
        finishTime = LocalDateTime.now();
    }

}
