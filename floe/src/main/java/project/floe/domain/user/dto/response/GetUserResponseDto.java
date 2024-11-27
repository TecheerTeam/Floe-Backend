package project.floe.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.user.entity.User;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponseDto {

    private String email;
    private String nickname;
    private int experience;
    private int age;
    private String profileImage;
    private String field;


    public static GetUserResponseDto from(User user) {
        return GetUserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .experience(user.getExperience())
                .age(user.getAge())
                .profileImage(user.getProfileImage())
                .field(user.getField())
                .build();
    }
}
