package org.example.stockradar.feature.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hyun7en
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentRequestDto {
    private Long commentId;
    private Long boardId;
    private String content;
}
