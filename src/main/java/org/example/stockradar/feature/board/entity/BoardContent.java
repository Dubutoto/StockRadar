package org.example.stockradar.feature.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long contentId;

    @Column( nullable = false, columnDefinition = "TEXT")
    private String content;

    // Board와 1:1 관계 (board_id는 고유해야 함)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;

    // 도메인 메서드로 업데이트 로직 제공
    public void updateContent(String content) {
        this.content = content;
    }
}