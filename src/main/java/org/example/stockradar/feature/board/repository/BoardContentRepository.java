package org.example.stockradar.feature.board.repository;

import org.example.stockradar.feature.board.entity.BoardContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardContentRepository extends JpaRepository<BoardContent, Long> {

}
