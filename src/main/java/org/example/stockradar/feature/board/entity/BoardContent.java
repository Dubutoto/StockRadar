package org.example.stockradar.feature.board.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Hyun7en
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column( nullable = false, columnDefinition = "TEXT")
    private String content;

    // Board와 1:1 관계 (board_id는 고유해야 함)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;

    //도메인 메서드로 insert 로직 제공
    public void linkBoard(Board board) {
        this.board = board;
    }

    // 도메인 메서드로 update 로직 제공
    public void updateContent(String content) {
        this.content = content;
    }


}