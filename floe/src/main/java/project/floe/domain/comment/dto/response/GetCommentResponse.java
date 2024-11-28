package project.floe.domain.comment.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.comment.entity.Comment;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCommentResponse {

    private Long commentId;
    private Long recordId;
    private Long userId;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;

    public static GetCommentResponse from(Comment comment) {
        return GetCommentResponse.builder()
                .commentId(comment.getId())
                .recordId(comment.getRecordId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}