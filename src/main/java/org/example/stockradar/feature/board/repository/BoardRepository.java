package org.example.stockradar.feature.board.repository;

import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Hyun7en
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 목록 조회: 모든 Board를 DTO로 반환 (목록 페이지용)
    @Query("SELECT new org.example.stockradar.feature.board.dto.BoardResponseDto(" +
            "b.boardId, b.boardTitle, bc.content, b.viewCount, b.commentCount, b.createdAt, b.updatedAt, m.memberCode,m.memberId, m.userName) " +
            "FROM Board b " +
            "JOIN b.boardContent bc " +
            "JOIN b.member m " +
            "WHERE b.deletedAt IS NULL " +
            "ORDER BY b.createdAt DESC")
    Page<BoardResponseDto> findBoardListDto(Pageable pageable);

    // 상세 조회: boardId를 기반으로 BoardResponseDto 반환 (Board, BoardContent, Member join)
    @Query("SELECT new org.example.stockradar.feature.board.dto.BoardResponseDto(" +
            "b.boardId, b.boardTitle, bc.content, b.viewCount, b.commentCount, b.createdAt, b.updatedAt, m.memberCode, m.memberId, m.userName) " +
            "FROM Board b " +
            "JOIN b.boardContent bc " +
            "JOIN b.member m " +
            "WHERE b.boardId = :boardId " +
            "AND b.deletedAt IS NULL")
    BoardResponseDto findBoardResponseDtoByBoardId(@Param("boardId") Long boardId);

    // 검색: 제목에 keyword가 포함된 결과를 DTO로 반환
    @Query("SELECT new org.example.stockradar.feature.board.dto.BoardResponseDto(" +
            "b.boardId, b.boardTitle, bc.content, b.viewCount, b.commentCount, b.createdAt, b.updatedAt, m.memberCode,m.memberId, m.userName) " +
            "FROM Board b " +
            "JOIN b.boardContent bc " +
            "JOIN b.member m " +
            "WHERE b.boardTitle LIKE %:keyword% " +
            "AND b.deletedAt IS NULL " +
            "ORDER BY b.createdAt DESC")
    Page<BoardResponseDto> findBoardListDtoByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 3개월마다 삭제할 게시물 찾기
    @Query("SELECT b FROM Board b WHERE b.deletedAt IS NOT NULL AND b.deletedAt <= :threeMonthsAgo")
    List<Board> findAllByDeletedAtBefore(LocalDateTime threeMonthsAgo);



}
