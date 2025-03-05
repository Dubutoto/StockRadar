package org.example.stockradar.feature.board.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }
}
