package project.floe.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
// 자체 로그인에 사용되는 dto
public class UserSignUpRequest {

    @NotBlank(message = "password cannot be blank")
    private String password;

    @NotBlank(message = "nickname cannot be blank")
    private String nickname;

    @NotBlank(message = "email cannot be blank")
    private String email;

    private int experience;
    private int age;
    private String profileImage;
    private String field;
}
