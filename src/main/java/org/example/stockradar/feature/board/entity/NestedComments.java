package org.example.stockradar.feature.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;

import java.time.LocalDateTime;

@Entity
@Table(name = "Nested_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NestedComments {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long nestedCommentsId;



    @Column( nullable = false, length = 225)
    private String nestedCommentsContent;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    //댓글과 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    private Comments comments;

    //맴버와 관계설정 해
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    //계시판과 관계설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}