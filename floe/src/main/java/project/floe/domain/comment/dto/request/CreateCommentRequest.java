package project.floe.domain.comment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCommentRequest {
    @NotNull(message = "기록 ID는 필수입니다.")
    private Long recordId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @Size(min = 1, max = 200, message = "댓글 내용은 1자 이상 200자 이하로 작성해주세요.")
    private String content;

    private Long parentId;

}