package org.example.stockradar.feature.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.entity.Comments;
import org.example.stockradar.feature.board.entity.NestedComments;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_code")
    private Long memberCode;  // PK, Auto Increment

    @Column(name = "member_id", unique = true, nullable = false, length = 50)
    private String memberId;  // 이메일/아이디 (Unique)

    @Column(name = "member_pw", nullable = false)
    private String memberPw;  // 비밀번호 (BCrypt 등 해싱된 값)

    @Column(name = "user_name", nullable = false)
    private String userName;  // 사용자명

    @Column(name = "member_phone")
    private String memberPhone; // 연락처(선택)

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;  // MEMBER, ADMIN

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.MEMBER; // 회원가입 시 기본 MEMBER
        }
    }
    //고객문의 관계설정(진오)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CustomerInquiry> customerInquiries = new ArrayList<>();

    //게시판 관계설정(진오)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>();

    //댓글과 관계설정(진오)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comments> comments = new ArrayList<>();

    //대댓글과 관계설정(진오)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<NestedComments> nestedComments = new ArrayList<>();


    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

    // 편의상 추가한 생성자
    public Member(String memberId, String memberPw, String userName, String memberPhone, Role role) {
        this.memberId = memberId;
        this.memberPw = memberPw;
        this.userName = userName;
        this.memberPhone = memberPhone;
        this.role = role;
    }
}