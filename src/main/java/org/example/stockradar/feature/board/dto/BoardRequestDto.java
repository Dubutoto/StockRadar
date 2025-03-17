package org.example.stockradar.feature.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * @author Hyun7en
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String boardTitle;

    // 게시글 상세 내용을 담는 필드 (BoardContent와 연계)
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

}
