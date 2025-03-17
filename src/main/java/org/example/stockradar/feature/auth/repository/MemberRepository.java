package org.example.stockradar.feature.auth.repository;

import org.example.stockradar.feature.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMemberId(String memberId);

    // 여러 회원을 반환하기 위해 List<Member> 메서드를 사용
    List<Member> findAllByMemberPhone(String memberPhone);

}
