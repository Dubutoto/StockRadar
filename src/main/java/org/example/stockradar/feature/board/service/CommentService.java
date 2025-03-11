package org.example.stockradar.feature.board.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.board.dto.CommentRequestDto;
import org.example.stockradar.feature.board.dto.CommentResponseDto;
import org.example.stockradar.feature.board.entity.Board;
import org.example.stockradar.feature.board.entity.Comments;
import org.example.stockradar.feature.board.repository.BoardRepository;
import org.example.stockradar.feature.board.repository.CommentRepository;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * author Hyun7en
 */

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 댓글 삽입: JWT를 통해 전달받은 memberId를 사용하여 Board와 Member 엔터티를 조회 후 설정
    public CommentRequestDto insertComment(CommentRequestDto commentRequestDto, String memberId) {
        // boardId를 이용해 Board 엔터티 조회
        Board board = boardRepository.findById(commentRequestDto.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + commentRequestDto.getBoardId()));

        // 전달받은 memberId를 이용해 Member 엔터티 조회
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new RuntimeException("Member not found with id: " + memberId);
        }

        // Comments 엔터티 생성 (setter 없이 빌더 패턴과 도메인 메서드 활용)
        Comments comment = Comments.builder()
                .content(commentRequestDto.getContent())
                .board(board)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        Comments savedComment = commentRepository.save(comment);

        return CommentRequestDto.builder()
                .boardId(savedComment.getBoard().getBoardId())
                .content(savedComment.getContent())
                .build();
    }

    // 댓글 목록 조회 (페이징 처리, 소프트 삭제된 댓글 제외)
    public Page<CommentResponseDto> getComments(Pageable pageable) {
        return commentRepository.findByDeletedAtIsNull(pageable)
                .map(comment -> CommentResponseDto.builder()
                        .userName(comment.getMember() != null ? comment.getMember().getUserName() : "anonymous")
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build());
    }

    // 댓글 소프트 삭제: 도메인 메서드 softDelete()를 호출하여 deletedAt 필드를 갱신
    public void softDeleteComment(Long commentId) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        comment.softDelete();
        commentRepository.save(comment);
    }

    // 댓글 업데이트: 도메인 메서드 updateContent()를 호출하여 내용 변경
    public CommentRequestDto updateComment(Long commentId, CommentRequestDto commentRequestDto) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        comment.updateContent(commentRequestDto.getContent());
        Comments updatedComment = commentRepository.save(comment);
        return CommentRequestDto.builder()
                .boardId(updatedComment.getBoard() != null ? updatedComment.getBoard().getBoardId() : commentRequestDto.getBoardId())
                .content(updatedComment.getContent())
                .build();
    }
}
