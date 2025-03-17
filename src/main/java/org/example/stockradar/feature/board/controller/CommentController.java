package org.example.stockradar.feature.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockradar.feature.board.dto.CommentDeleteRequestDto;
import org.example.stockradar.feature.board.dto.CommentRequestDto;
import org.example.stockradar.feature.board.dto.CommentResponseDto;
import org.example.stockradar.feature.board.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hyun7en
 */

//RestController 사용해서 해보기
@Slf4j
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 삽입 (Insert)
    @PostMapping("insert")
    public ResponseEntity<CommentRequestDto> insertComment(@RequestBody CommentRequestDto commentDto, Authentication authentication) {

        log.info("Insert comment request: {}", commentDto);

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // 인증된 사용자 ID 가져오기 (예: JWT의 subject를 사용하여 memberId 반환)
        String memberId = String.valueOf(authentication.getName());
        CommentRequestDto savedComment = commentService.insertComment(commentDto, memberId);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    // 댓글 목록 조회 (페이징 처리 포함)
    @GetMapping("read")
    public ResponseEntity<Page<CommentResponseDto>> getComments(@RequestParam Long boardId,@RequestParam(defaultValue = "0") int page) {
        System.out.println("boardId: " + boardId);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<CommentResponseDto> commentPage = commentService.getComments(boardId,pageable);
        return new ResponseEntity<>(commentPage, HttpStatus.OK);
    }

    @PostMapping("delete")
    public ResponseEntity<Map<String, Object>> softDeleteComment(@RequestBody CommentDeleteRequestDto request) {
        boolean result = commentService.softDeleteComment(request);

        log.info("Delete comment request: {}", request);

        Map<String, Object> response = new HashMap<>();
        if(result) {
            response.put("success", true);
            response.put("message", "댓글 삭제에 성공했습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("success", false);
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
