package org.example.stockradar.feature.CustomerInquiry.repository;

import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInquiryRepository extends JpaRepository<CustomerInquiry, Long> {

    List<CustomerInquiry> findByInquiryStatus(int inquiryStatus);
}
