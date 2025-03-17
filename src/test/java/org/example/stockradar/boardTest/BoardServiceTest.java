package org.example.stockradar.boardTest;

import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.example.stockradar.feature.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hyun7en
 */

@SpringBootTest
@Transactional
public class BoardServiceTest {

    @Autowired
    private BoardService boardService; // 서비스 계층 테스트

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("3개월 초과된 Soft Delete 게시글이 정상 삭제되는지 확인")
    public void testDeleteOldSoftDeletedBoards() {
        // 1. 테스트 데이터 준비 (빌더 패턴 사용)
        Board oldBoard = Board.builder()
                .boardTitle("Old Soft Deleted Post")
                .deletedAt(LocalDateTime.now().minusMonths(3).minusDays(1)) // 3개월 초과
                .build();
        boardRepository.save(oldBoard);

        // 2. 3개월 미만 게시글 추가 (삭제되지 않아야 함)
        Board recentBoard = Board.builder()
                .boardTitle("Recent Soft Deleted Post")
                .deletedAt(LocalDateTime.now().minusMonths(2)) // 3개월 미만
                .build();
        boardRepository.save(recentBoard);

        // 3. 스케줄러 실행 (직접 호출)
        boardService.deleteOldSoftDeletedBoards();

        // 4. 삭제된 게시글 확인
        boolean oldBoardExists = boardRepository.existsById(oldBoard.getBoardId());
        boolean recentBoardExists = boardRepository.existsById(recentBoard.getBoardId());

        // 5. 검증
        assertFalse(oldBoardExists, "3개월 초과된 게시글은 삭제되어야 합니다.");
        assertTrue(recentBoardExists, "3개월 미만의 게시글은 유지되어야 합니다.");

        System.out.println("testDeleteOldSoftDeletedBoards: 테스트 성공");
    }

    @Test
    @DisplayName("삭제할 게시글이 없을 경우에도 정상 실행되는지 확인")
    public void testNoBoardsToDelete() {
        // 1. 초기 데이터 없음
        boardRepository.deleteAll();

        // 2. 스케줄러 실행 (예외 없이 실행되는지 확인)
        assertDoesNotThrow(() -> boardService.deleteOldSoftDeletedBoards());

        System.out.println("testNoBoardsToDelete: 테스트 성공");
    }

    @Test
    @DisplayName("삭제 중 예외 발생 시 트랜잭션이 롤백되는지 확인")
    public void testDeleteRollbackOnException() {
        // 1. 테스트 데이터 준비 (빌더 사용)
        Board board = Board.builder()
                .boardTitle("Rollback Test")
                .deletedAt(LocalDateTime.now().minusMonths(3).minusDays(1)) // 3개월 초과
                .build();
        boardRepository.save(board);

        // 2. boardRepository의 deleteAll 메서드 호출 시 예외 발생하도록 mock 객체 생성
        BoardRepository mockRepository = Mockito.mock(BoardRepository.class);
        Mockito.doThrow(new RuntimeException("DB 삭제 오류"))
                .when(mockRepository).deleteAll(Mockito.anyList());

        // 3. ReflectionTestUtils를 사용하여 boardService의 boardRepository 필드를 mockRepository로 대체
        ReflectionTestUtils.setField(boardService, "boardRepository", mockRepository);

        // 4. 스케줄러 실행 시 예외 발생 여부 확인
        assertThrows(RuntimeException.class, () -> boardService.deleteOldSoftDeletedBoards());

        // 5. 테스트 후 원래 boardRepository로 복구 (옵션)
        ReflectionTestUtils.setField(boardService, "boardRepository", boardRepository);

        // 6. 트랜잭션 롤백 검증 (게시글이 여전히 남아 있어야 함)
        // 예외 발생으로 인한 롤백 처리 덕분에 board는 DB에 남아 있어야 함
        assertTrue(boardRepository.existsById(board.getBoardId()), "예외 발생 시 게시글이 롤백되어야 합니다.");

        System.out.println("testDeleteRollbackOnException: 테스트 성공");
    }
}
