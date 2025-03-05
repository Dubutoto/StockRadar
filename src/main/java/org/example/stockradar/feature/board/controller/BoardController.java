package org.example.stockradar.feature.board.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.example.stockradar.feature.board.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("insertWithContent")
    public ResponseEntity<Board> insertWithContent(@RequestBody Board board) {
        // board 객체에 boardContent가 포함되어 있다고 가정
        Board savedBoard = boardService.saveBoard(board);
        return ResponseEntity.ok(savedBoard);
    }
}
