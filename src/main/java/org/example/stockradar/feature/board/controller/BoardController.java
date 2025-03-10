package org.example.stockradar.feature.board.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.dto.BoardRequestDto;
import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String getBoardList(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지당 10개
        Page<Board> boardPage = boardService.findAll(pageable);
        model.addAttribute("list", boardPage);
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
     * 게시글 작성 후 db로 데이터 전송
     *
     * @return 게시글 목록 board/boardWrite
     */
    @PostMapping("insert")
    public String boardInsert(BoardRequestDto boardRequestDto) {
        boardService.saveBoard(boardRequestDto);
        return "redirect:/board/list";
    }

    /**
     * 게시글 상세 페이지로 이동합니다.
     *
     * @return 게시글 상세 정보를 보여주는 뷰 이름 "board/boardDetail"
     */
    @GetMapping("detail")
    public String boardDetail(@RequestParam Long boardId, Model model) {
        BoardResponseDto boardResponseDto = boardService.findBoardResponseDtoByBoardId(boardId);
        model.addAttribute("board", boardResponseDto);
        return "board/detail";
    }

    /**
     * 게시글 수정/삭제 폼 페이지로 이동합니다.
     *
     * @return 게시글 수정/삭제를 위한 폼 뷰 이름 "board/boardForm"
     */
    @GetMapping("form")
    public String boardForm(@RequestParam Long boardId, Model model) {
        BoardResponseDto boardResponseDto = boardService.findBoardResponseDtoByBoardId(boardId);
        model.addAttribute("board", boardResponseDto);
        return "board/form";
    }

    /**
     * 게시글 수정 처리
     * @param boardId
     * @param boardRequestDto
     * @return
     */
    @PostMapping("update")
    public String boardUpdate(@RequestParam Long boardId, BoardRequestDto boardRequestDto) {
        boardService.updateBoard(boardId, boardRequestDto);
        return "redirect:/board/detail?boardId=" + boardId;
    }

    /**
     * 게시글 삭제 처리
     * @param boardId
     * @return
     */
    @PostMapping("delete")
    public String boardDelete(@RequestParam Long boardId) {
        boardService.deleteBoard(boardId);
        return "redirect:/board/list";
    }

    /**
     * 게시글 제목 검색
     * @param keyword
     * @param page
     * @param model
     * @return
     */
    @GetMapping("search")
    public String boardSearch(@RequestParam String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Board> boardPage = boardService.searchBoards(keyword, pageable);
        model.addAttribute("list", boardPage);
        model.addAttribute("keyword", keyword);
        return "board/search";
    }


}
