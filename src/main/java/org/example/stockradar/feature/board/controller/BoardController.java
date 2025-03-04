package org.example.stockradar.feature.board.controller;

import org.example.stockradar.feature.board.entity.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("board")
public class BoardController {

    @PostMapping("insert")
    public ResponseEntity<Board> insert(@RequestBody Board board) {
        return ResponseEntity.ok(board);

    }
}
