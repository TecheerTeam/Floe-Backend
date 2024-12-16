package project.floe.domain.user_follow.dto.response;

import java.util.List;

public record GetUserFollowerListResponse(List<GetUserFollowUserResponse> userFollowerList) {

}
