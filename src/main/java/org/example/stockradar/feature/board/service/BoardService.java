package org.example.stockradar.feature.board.service;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.board.dto.BoardRequestDto;
import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.entity.BoardContent;
import org.example.stockradar.feature.board.repository.BoardContentRepository;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * @author Hyun7en
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardContentRepository boardContentRepository;

    // 페이징 처리된 게시글 목록 조회
    public Page<Board> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    // 게시글 등록 (Board, BoardContent, 그리고 작성자(Member)를 저장)
    public Board saveBoard(BoardRequestDto boardRequestDto) {
        // 실제 환경에서는 SecurityContext에서 인증된 사용자 정보를 가져옵니다.
        Member member = new Member();
        member.setMemberCode(1L);  // 예시 값
        member.setUserName("DefaultUser");

        // Board 엔터티 생성 (빌더 사용)
        Board board = Board.builder()
                .boardTitle(boardRequestDto.getBoardTitle())
                .boardCategory(boardRequestDto.getBoardCategory())
                .member(member)
                .build();
        board = boardRepository.save(board);

        // BoardContent 엔터티 생성 (빌더 사용) 및 연관관계 설정
        BoardContent boardContent = BoardContent.builder()
                .content(boardRequestDto.getBoardContent())
                .board(board)
                .build();
        boardContentRepository.save(boardContent);

        return board;
    }

    // 게시글 상세 조회 - DTO 프로젝션으로 변환하여 반환
    public BoardResponseDto findBoardResponseDtoByBoardId(Long boardId) {
        return boardRepository.findBoardResponseDtoByBoardId(boardId);
    }

    // 게시글 수정 처리
    public Board updateBoard(Long boardId, BoardRequestDto boardRequestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        // Board 엔터티 업데이트 (도메인 메서드 사용)
        board.updateBoard(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardCategory());
        board = boardRepository.save(board);

        // BoardContent 업데이트 - setter 대신 도메인 메서드 updateContent() 사용
        BoardContent boardContent = board.getBoardContent();
        if (boardContent != null) {
            boardContent.updateContent(boardRequestDto.getBoardContent());
            boardContentRepository.save(boardContent);
        }
        return board;
    }

    // 게시글 삭제 처리
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    // 제목 검색 (페이징 처리)
    public Page<Board> searchBoards(String keyword, Pageable pageable) {
        return boardRepository.findByBoardTitleContaining(keyword, pageable);
    }
}
