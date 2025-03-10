package org.example.stockradar.feature.board.repository;

import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Hyun7en
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목에 해당 키워드를 포함하는 게시글 검색 (페이징 처리)
    Page<Board> findByBoardTitleContaining(String keyword, Pageable pageable);

    // DTO 반환을 위한 JPQL: Board, BoardContent, Member를 조인하여 필요한 필드만 조회
    @Query("SELECT new org.example.stockradar.feature.board.dto.BoardResponseDto(" +
            "b.boardId, b.boardTitle, b.boardCategory, bc.content, b.createdAt, b.updatedAt, m.memberCode, m.userName) " +
            "FROM Board b " +
            "JOIN b.boardContent bc " +
            "JOIN b.member m " +
            "WHERE b.boardId = :boardId")
    BoardResponseDto findBoardResponseDtoByBoardId(@Param("boardId") Long boardId);
}
