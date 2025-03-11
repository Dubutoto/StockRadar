package org.example.stockradar.feature.board.service;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.board.dto.BoardRequestDto;
import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.entity.BoardContent;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


/**
 * @author Hyun7en
 */

@lombok.extern.slf4j.Slf4j
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 게시글 목록 조회 (DTO 프로젝션 사용)
     */
    public Page<BoardResponseDto> findBoardListDto(Pageable pageable) {
        return boardRepository.findBoardListDto(pageable);
    }

    /**
     * 게시글 등록 처리
     */
    @Transactional(rollbackFor = Exception.class)
    public Board saveBoard(BoardRequestDto boardRequestDto) {
        // 실제 환경에서는 SecurityContext에서 인증된 사용자 정보를 가져옵니다.
        Member member = new Member();
        member.setMemberCode(1L);      // 예시 값
        member.setUserName("DefaultUser");

        // Board 엔터티 생성 (빌더 사용)
        Board board = Board.builder()
                .boardTitle(boardRequestDto.getBoardTitle())
                .member(member)
                .build();

        // BoardContent 엔터티 생성 (빌더 사용) 및 연관관계 설정
        BoardContent boardContent = BoardContent.builder()
                .content(boardRequestDto.getContent())
                .build();
        board.addBoardContent(boardContent);  // 양쪽 연관관계를 설정하는 편의 메서드

        // Cascade 설정 덕분에 board와 연관된 boardContent가 함께 저장됨
        return boardRepository.save(board);
    }

    /**
     * 게시글 상세 조회 (DTO 프로젝션 사용)
     * 조회 시 viewCount 증가 로직을 함께 처리
     */
    @Transactional(rollbackFor = Exception.class)
    public BoardResponseDto findBoardResponseDtoByBoardId(Long boardId) {
        // 1) 게시글 상세 정보(DTO) 조회
        BoardResponseDto dto = boardRepository.findBoardResponseDtoByBoardId(boardId);
        if (dto == null) {
            throw new RuntimeException("Board not found: " + boardId);
        }

        // 2) viewCount 증가 처리 (엔티티 직접 조회 후 증가)
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        // 서비스 계층에서 처리
        long currentViews = board.getViewCount() == null ? 0 : board.getViewCount();
        board.incrementViewCount();

        // 3) save 호출 또는 JPA 영속성 컨텍스트에 의해 자동 반영
        // boardRepository.save(board); // 필요에 따라 호출
        return dto;
    }

    /**
     * 게시글 수정 처리
     */
    public Board updateBoard(Long boardId, BoardRequestDto boardRequestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        // Board 엔터티의 도메인 메서드를 통해 제목,내용 업데이트 (필요에 따라 추가 업데이트 로직 작성)
        board.updateBoard(boardRequestDto.getBoardTitle());
        // BoardContent 업데이트: 엔터티 내 도메인 메서드 updateContent() 사용
        if (board.getBoardContent() != null) {
            board.getBoardContent().updateContent(boardRequestDto.getContent());
        }
        board = boardRepository.save(board);
        return board;
    }

    /**
     * 게시글 삭제 처리
     */
    public boolean deleteBoard(Long boardId, String password) {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (!boardOpt.isPresent()) {
            return false;
        }

        Board board = boardOpt.get();
        Member member = board.getMember();

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getMemberPw())) {
            return false;
        }

        // 실제 삭제 대신 `deletedAt` 업데이트 (소프트 딜리트)
        board.softDelete();
        boardRepository.save(board); // 변경 사항 저장

        return true;
    }

    /**
     * 매일 새벽 2시에 3개월 지난 게시글 완전 삭제 => 정책에 공지하기
     */
    @Scheduled(cron = "0 0 2 * * ?")  // 매일 새벽 2시 실행
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldSoftDeletedBoards() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        // 3개월 이상 지난 Soft Deleted 게시글 조회
        List<Board> boardsToDelete = boardRepository.findAllByDeletedAtBefore(threeMonthsAgo);

        boardRepository.deleteAll(boardsToDelete);

        log.info("Deleted old soft deleted boards");
    }

    /**
     * 게시글 제목 검색 (DTO 프로젝션 사용)
     */
    public Page<BoardResponseDto> searchBoards(String keyword, Pageable pageable) {
        return boardRepository.findBoardListDtoByKeyword(keyword, pageable);
    }
}
