package org.example.stockradar.feature.board.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("boardViewController")
@RequiredArgsConstructor
@RequestMapping("board")
public class ViewController {

    private final BoardService boardService;

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

    // 게시글 저장 후 목록 페이지로 리다이렉트
    @PostMapping("insertWithContent")
    public String insertWithContent(@ModelAttribute Board board) {
        boardService.saveBoard(board);
        return "redirect:/board/boardList";  // 저장 후 게시글 목록 페이지로 이동
    }

}
