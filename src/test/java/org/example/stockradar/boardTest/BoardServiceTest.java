package org.example.stockradar.boardTest;

import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.example.stockradar.feature.board.service.BoardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 삽입 (3개월 + 1일 지난 soft delete 데이터)
        LocalDateTime oldDeletedTime = LocalDateTime.now().minusMonths(3).minusDays(1);
        Board oldBoard = Board.builder()
                .boardTitle("오래된 게시글")
                .viewCount(10L)
                .commentCount(2L)
                .createdAt(oldDeletedTime)
                .deletedAt(oldDeletedTime)  // Soft Delete 처리
                .build();
        boardRepository.save(oldBoard);

        // 3개월이 지나지 않은 Soft Deleted 데이터 (삭제 대상이 아님)
        LocalDateTime recentDeletedTime = LocalDateTime.now().minusMonths(2);
        Board recentBoard = Board.builder()
                .boardTitle("삭제 대상이 아닌 게시글")
                .viewCount(5L)
                .commentCount(1L)
                .createdAt(recentDeletedTime)
                .deletedAt(recentDeletedTime)
                .build();
        boardRepository.save(recentBoard);
    }

    @Test
    @Transactional
    void testDeleteOldSoftDeletedBoards() {
        // 삭제 전 데이터 개수 확인
        long beforeCount = boardRepository.count();
        Assertions.assertTrue(beforeCount > 0, "테스트 데이터가 있어야 합니다.");

        // 삭제 메서드 실행
        boardService.deleteOldSoftDeletedBoards();

        // 삭제 후, 3개월 이상 지난 데이터가 제거되었는지 확인
        long afterCount = boardRepository.count();
        List<Board> remainingBoards = boardRepository.findAll();

        Assertions.assertTrue(afterCount < beforeCount, "게시글이 삭제되어야 합니다.");
        Assertions.assertTrue(remainingBoards.stream()
                        .noneMatch(board -> board.getDeletedAt() != null && board.getDeletedAt().isBefore(LocalDateTime.now().minusMonths(3))),
                "3개월 이상 지난 게시글이 삭제되었어야 합니다.");
    }
}



