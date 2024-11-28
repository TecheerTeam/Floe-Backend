package project.floe.domain.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.floe.domain.user.entity.User;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponseDto {

    private String nickname;
    private String email;
    private int experience;
    private int age;
    private String profileImage;
    private String field;

    public static UpdateUserResponseDto from(User user) {
        return UpdateUserResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .experience(user.getExperience())
                .age(user.getAge())
                .profileImage(user.getProfileImage())
                .field(user.getField())
                .build();
    }
}
