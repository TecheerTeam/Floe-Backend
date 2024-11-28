package project.floe.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    private Long parentId; // 부모 댓글 ID (답글인 경우 null 가능)

}