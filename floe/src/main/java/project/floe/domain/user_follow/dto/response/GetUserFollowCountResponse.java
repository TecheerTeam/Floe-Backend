package project.floe.domain.user_follow.dto.response;

import lombok.Getter;

@Getter
public class GetUserFollowCountResponse {
    private final long followerCount;
    private final long followingCount;

    public GetUserFollowCountResponse(long followingCount, long followerCount) {
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }
}