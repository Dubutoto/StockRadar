package org.example.stockradar.feature.auth.repository;

import org.example.stockradar.feature.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMemberId(String memberId);
}
