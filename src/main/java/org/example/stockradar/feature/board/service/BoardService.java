package org.example.stockradar.feature.board.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.repository.BoardContentRepository;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * @author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardContentRepository boardContentRepository;

    public Page<Board> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }


}
