package org.example.stockradar.feature.board.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.repository.BoardContentRepository;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardContentRepository boardContentRepository;

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }
}
