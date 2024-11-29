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
    private GetCommentUserResponse user;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;

    public static GetCommentResponse from(Comment comment) {
        return GetCommentResponse.builder()
                .commentId(comment.getId())
                .user(GetCommentUserResponse.from(comment.getUser()))
                .content(comment.getContent())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}