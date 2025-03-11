package org.example.stockradar.feature.board.repository;

import org.example.stockradar.feature.board.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author Hyun7en
 */

public interface CommentRepository extends JpaRepository<Comments, Long> {
    Page<Comments> findByDeletedAtIsNull(Pageable pageable);
}
