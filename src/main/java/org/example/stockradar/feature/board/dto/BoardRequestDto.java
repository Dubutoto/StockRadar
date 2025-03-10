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

    @NotBlank(message = "카테고리는 필수입니다.")
    private String boardCategory;

    // 게시글 상세 내용을 담는 필드 (BoardContent와 연계)
    private String boardContent;

}
