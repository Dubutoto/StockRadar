package org.example.stockradar.feature.board.repository;

import org.example.stockradar.feature.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Hyun7en
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

}
