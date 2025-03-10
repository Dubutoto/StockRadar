package org.example.stockradar.feature.board.repository;

import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.entity.BoardContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Hyun7en
 */
public interface BoardContentRepository extends JpaRepository<BoardContent, Long> {


}
