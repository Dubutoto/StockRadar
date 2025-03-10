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
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long boardId;

    @Column(nullable = false, length = 100)
    private String boardTitle;

    // 조회수
    @Column(nullable = false, columnDefinition = "0L")
    private Long viewCount = 0L;

    // 댓글 수
    @Column(nullable = false, columnDefinition = "0L")
    private Long commentCount = 0L;

    @Column(nullable = false ,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime deletedAt;

    //멤버와 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberCode")
    private Member member;

    // Board와 Comments는 1:N 관계 (Comments 테이블의 board_id가 FK)
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comments> comments = new ArrayList<>();

    // Board와 Board_Content는 1:1 관계로 가정 (Board_Content의 board_id가 유니크하다고 가정)
    @OneToOne(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BoardContent boardContent;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 엔터티 업데이트를 위한 도메인 메서드
    public void updateBoard(String boardTitle, String boardCategory) {
        this.boardTitle = boardTitle;
    }

    // BoardContent 연결을 위한 편의 메서드
    public void addBoardContent(BoardContent boardContent) {
        this.boardContent = boardContent;
    }

    // 조회수 증가 로직
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1L : this.viewCount + 1L;
    }

    // 댓글 수 증가
    public void incrementCommentCount() {
        this.commentCount++;
    }

    // 댓글 수 감소
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}