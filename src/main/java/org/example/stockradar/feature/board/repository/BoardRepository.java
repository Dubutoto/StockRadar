package org.example.stockradar.feature.board.repository;

import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Hyun7en
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 목록 조회: 모든 Board를 DTO로 반환 (목록 페이지용)
    @Query("SELECT new org.example.stockradar.feature.board.dto.BoardResponseDto(" +
            "b.boardId, b.boardTitle, bc.content, b.viewCount, b.createdAt, b.updatedAt, m.memberCode, m.userName) " +
            "FROM Board b " +
            "JOIN b.boardContent bc " +
            "JOIN b.member m "+
            "ORDER BY b.createdAt DESC")
    Page<BoardResponseDto> findBoardListDto(Pageable pageable);

    // 상세 조회: boardId를 기반으로 BoardResponseDto 반환 (Board, BoardContent, Member join)
    @Query("SELECT new org.example.stockradar.feature.board.dto.BoardResponseDto(" +
            "b.boardId, b.boardTitle, bc.content, b.viewCount, b.createdAt, b.updatedAt, m.memberCode, m.userName) " +
            "FROM Board b " +
            "JOIN b.boardContent bc " +
            "JOIN b.member m " +
            "WHERE b.boardId = :boardId")
    BoardResponseDto findBoardResponseDtoByBoardId(@Param("boardId") Long boardId);

    // 검색: 제목에 keyword가 포함된 결과를 DTO로 반환
    @Query("SELECT new org.example.stockradar.feature.board.dto.BoardResponseDto(" +
            "b.boardId, b.boardTitle, bc.content, b.viewCount, b.createdAt, b.updatedAt, m.memberCode, m.userName) " +
            "FROM Board b " +
            "JOIN b.boardContent bc " +
            "JOIN b.member m " +
            "WHERE b.boardTitle LIKE %:keyword% " +
            "ORDER BY b.createdAt DESC")
    Page<BoardResponseDto> findBoardListDtoByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
