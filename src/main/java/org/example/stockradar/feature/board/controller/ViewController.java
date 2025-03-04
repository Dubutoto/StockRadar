package org.example.stockradar.feature.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("boardViewController")
@RequiredArgsConstructor
@RequestMapping("board")
public class ViewController {

    @GetMapping("boardList")
    public String boardList() {
        return "board/boardList";
    }

    @GetMapping("boardWrite")
    public String boardWrite() {
        return "board/boardWrite";
    }

    @GetMapping("boardDetail")
    public String boardDetail() {
        return "board/boardDetail";
    }

    @GetMapping("boardForm")
    public String boardForm() {
        return "board/boardForm";
    }

}
