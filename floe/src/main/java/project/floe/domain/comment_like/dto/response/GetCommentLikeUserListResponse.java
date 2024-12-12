package project.floe.domain.comment_like.dto.response;

import java.util.List;

public record GetCommentLikeUserListResponse(List<GetCommentLikeUserResponse> commentLikeUsers) {
}
