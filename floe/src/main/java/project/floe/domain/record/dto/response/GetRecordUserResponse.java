package project.floe.domain.record.dto.response;

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
public class GetRecordUserResponse {
    private String nickname;
    private String email;

    public static GetRecordUserResponse from(User user) {
        return GetRecordUserResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
