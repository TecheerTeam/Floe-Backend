package project.floe.domain.comment.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.user.entity.User;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class GetCommentUserResponse {

    private String nickname;
    private String email;
    private String profileImage;

    public static GetCommentUserResponse from(User user) {
        return GetCommentUserResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();
    }


}
