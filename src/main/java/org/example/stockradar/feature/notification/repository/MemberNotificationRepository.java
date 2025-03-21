package org.example.stockradar.feature.notification.repository;

import org.example.stockradar.feature.notification.entity.MemberNotification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Hyun7en
 */
public interface MemberNotificationRepository extends JpaRepository<MemberNotification, Long> {

    long deleteByInterestProductIdAndMember_MemberId(Long interestProductId, String memberId);

}
