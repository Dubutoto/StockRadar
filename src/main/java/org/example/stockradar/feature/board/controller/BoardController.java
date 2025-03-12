package org.example.stockradar.feature.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.dto.BoardDeleteRequestDto;
import org.example.stockradar.feature.board.dto.BoardRequestDto;
import org.example.stockradar.feature.board.dto.BoardResponseDto;
import org.example.stockradar.feature.board.dto.CommentRequestDto;
import org.example.stockradar.feature.board.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hyun7en
 */

//게시판은 다양한 방법 써서 구현 해보기
@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 모든 게시글 목록을 조회하여 모델에 추가한 후, 게시글 목록 페이지로 이동합니다.
     */
    @GetMapping("list")
    public String getBoardList(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<BoardResponseDto> boardList = boardService.findBoardListDto(pageable);
        model.addAttribute("boardList", boardList);
        return "board/list";
    }

    /**
     * 게시글 작성 페이지로 이동합니다.
     */
    @GetMapping("write")
    public String boardWrite() {
        return "board/write";
    }

    /**
     * 게시글 작성 후 db로 데이터 전송
     */
    @PostMapping("insert")
    public String boardInsert(@Valid BoardRequestDto boardRequestDto, Authentication authentication) throws ResponseStatusException {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }

        // 인증된 사용자 ID 가져오기 (예: JWT의 subject를 사용하여 memberId 반환)
        String memberId = String.valueOf(authentication.getName());

        boardService.saveBoard(boardRequestDto,memberId);
        return "redirect:/board/list";
    }


    /**
     * 게시글 상세 페이지로 이동합니다.
     */
    @GetMapping("detail")
    public String boardDetail(@RequestParam Long boardId, Model model) {
        BoardResponseDto board = boardService.findBoardResponseDtoByBoardId(boardId);
        model.addAttribute("board", board);
        return "board/detail";
    }

    /**
     * 게시글 수정/삭제 폼 페이지로 이동합니다.
     */
    @GetMapping("form")
    public String boardForm(@RequestParam Long boardId, Model model) {
        BoardResponseDto board = boardService.findBoardResponseDtoByBoardId(boardId);
        model.addAttribute("board", board);
        return "board/form";
    }

    /**
     * 게시글 수정 처리
     */
    @PostMapping("update")
    public String boardUpdate(@RequestParam Long boardId, BoardRequestDto boardRequestDto) {
        boardService.updateBoard(boardId, boardRequestDto);
        return "redirect:/board/detail?boardId=" + boardId;
    }

    /**
     * 게시글 삭제 처리
     */
    @PostMapping("delete")
    public ResponseEntity<Map<String, Object>> deleteBoard(@RequestBody BoardDeleteRequestDto request) {
        Long boardId = request.getBoardId();
        String password = request.getPassword();

        boolean isDeleted = boardService.deleteBoard(boardId, password);
        Map<String, Object> response = new HashMap<>();

        if (!isDeleted) {
            response.put("success", false);
            response.put("message", "비밀번호가 올바르지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("success", true);
        response.put("message", "게시글이 삭제되었습니다.");
        response.put("redirectUrl", "/board/list");  // 리다이렉트 URL 추가

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 제목 검색
     */
    @GetMapping("search")
    public String boardSearch(@RequestParam String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        // DTO 반환으로 변경 (예: BoardResponseDto 또는 BoardListDto)
        Page<BoardResponseDto> boardList = boardService.searchBoards(keyword, pageable);
        model.addAttribute("boardList", boardList); // "board"로 통일
        model.addAttribute("keyword", keyword);
        return "board/list";
    }

}
