package org.example.stockradar.feature.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_code")
    private Long memberCode;  // PK, BIGINT, AUTO_INCREMENT

    @Column(name = "member_id", length = 50, nullable = false, unique = true)
    private String memberId;  // 아이디(이메일)

    @Column(name = "member_pw", nullable = false)
    private String memberPw;  // 비밀번호 (해싱된 값)

    @Column(name = "user_name", nullable = false)
    private String userName;  // 사용자명

    @Column(name = "member_phone")
    private String memberPhone; // 연락처 (선택)

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; // 권한 (MEMBER, ADMIN)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    //=== 엔티티 라이프사이클에 따라 날짜 자동 설정 ===//
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

    //=== 기본 생성자 ===//
    public Member() {}

    //=== Getter/Setter ===//
    public Long getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(Long memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberPw() {
        return memberPw;
    }

    public void setMemberPw(String memberPw) {
        this.memberPw = memberPw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }
}

