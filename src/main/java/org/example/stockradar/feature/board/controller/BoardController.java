package org.example.stockradar.feature.board.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * board list를 반환하는 메서드
     */
    @GetMapping("boardList")
    public String findAll(Model model) {
        List<Board> boardList = boardService.findAllBoards();
        model.addAttribute("boardList", boardList);
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
