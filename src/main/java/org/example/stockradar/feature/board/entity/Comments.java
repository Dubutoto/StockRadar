package org.example.stockradar.feature.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.stockradar.feature.auth.entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long commentId;

    @Column( nullable = false, length = 225)
    private String commentContent;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    //맴버와 관계설정 해
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    // Comments는 Board와 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

