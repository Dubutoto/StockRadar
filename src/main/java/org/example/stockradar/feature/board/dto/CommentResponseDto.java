package org.example.stockradar.feature.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * author Hyun7en
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentResponseDto {

    private long commentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //작성자 정보
    private String userName;

}
