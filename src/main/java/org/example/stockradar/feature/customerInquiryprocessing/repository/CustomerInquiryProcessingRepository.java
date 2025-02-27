package org.example.stockradar.feature.customerInquiryprocessing.repository;

import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.customerInquiryprocessing.entity.CustomerInquiryProcessiong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerInquiryProcessingRepository extends JpaRepository<CustomerInquiryProcessiong, Long> {
}

