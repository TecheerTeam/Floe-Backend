package project.floe.domain.comment_like.dto.response;

import lombok.Getter;
import project.floe.domain.user.entity.User;

@Getter
public class GetCommentLikeUserResponse {
    private final Long userId;
    private final String NickName;
    private final String profileImage;

    public GetCommentLikeUserResponse(User user) {
        this.userId = user.getId();
        this.NickName = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
