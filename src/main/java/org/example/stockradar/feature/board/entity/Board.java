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
    private Long boardId;

    @Column(nullable = false, length = 100)
    private String boardTitle;

    // 조회수
    @Column(nullable = false)
    private Long viewCount;

    // 댓글 수
    @Column(nullable = false)
    private Long commentCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onPrePersist() {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        if (this.commentCount == null) {
            this.commentCount = 0L;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //멤버와 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberCode")
    private Member member;

    // Board와 Board_Content는 1:1 관계로 가정 (Board_Content의 board_id가 유니크하다고 가정)
    @OneToOne(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BoardContent boardContent;

    // Board와 Comments는 1:N 관계 (Comments 테이블의 board_id가 FK)
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comments> comments = new ArrayList<>();

    // 엔터티 업데이트를 위한 도메인 메서드
    public void updateBoard(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    // BoardContent 연결을 위한 편의 메서드
    public void addBoardContent(BoardContent boardContent) {
        this.boardContent = boardContent;
        boardContent.linkBoard(this);
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

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();  //현재 시간으로 삭제 처리
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}