package org.example.stockradar.feature.board.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hyun7en
 */

@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 모든 게시글 목록을 조회하여 모델에 추가한 후, 게시글 목록 페이지로 이동합니다.
     *
     * @return 게시글 목록을 보여주는 뷰 이름 "board/boardList"
     */
    @GetMapping("list")
    public String getBoardList(@RequestParam(defaultValue = "0") int page ,Model model) {
        Pageable pageable = PageRequest.of(page, 10); //한 페이지 10개씩

        Page<Board> list = boardService.findAll(pageable);
        model.addAttribute("list", list);
        return "board/list";
    }

    /**
     * 게시글 작성 페이지로 이동합니다.
     *
     * @return 게시글 작성을 위한 뷰 이름 "board/boardWrite"
     */
    @GetMapping("write")
    public String boardWrite() {
        return "board/write";
    }

    /**
     * 게시글 상세 페이지로 이동합니다.
     *
     * @return 게시글 상세 정보를 보여주는 뷰 이름 "board/boardDetail"
     */
    @GetMapping("detail")
    public String boardDetail() {
        return "board/detail";
    }

    /**
     * 게시글 수정/삭제 폼 페이지로 이동합니다.
     *
     * @return 게시글 수정/삭제를 위한 폼 뷰 이름 "board/boardForm"
     */
    @GetMapping("form")
    public String boardForm() {
        return "board/form";
    }




}
