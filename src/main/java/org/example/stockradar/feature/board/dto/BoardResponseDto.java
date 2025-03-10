package org.example.stockradar.feature.board.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Hyun7en
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardResponseDto {

    private Long boardId;
    private String boardTitle;
    private String content;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 작성자 정보
    private Long memberCode;
    private String userName;
}
