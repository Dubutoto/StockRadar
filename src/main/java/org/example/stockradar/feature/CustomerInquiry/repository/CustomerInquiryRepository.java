package org.example.stockradar.feature.CustomerInquiry.repository;

import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerInquiryRepository extends JpaRepository<CustomerInquiry, Long> {

}
