package project.floe.domain.user_follow.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import project.floe.domain.user.entity.User;

@Getter
public class GetUserFollowUserResponse {
    private final Long userId;
    private final String nickName;
    private final String profileImage;
    @JsonProperty("isFollowed")
    private final boolean isFollowed;

    public GetUserFollowUserResponse(User user, boolean isFollowed) {
        this.userId = user.getId();
        this.nickName = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.isFollowed = isFollowed;
    }
    //JPA용 쿼리
    public GetUserFollowUserResponse(Long userId, String nickname, String profileImage, Boolean isFollowed) {
        this.userId = userId;
        this.nickName = nickname;
        this.profileImage = profileImage;
        this.isFollowed = isFollowed;
    }
}
