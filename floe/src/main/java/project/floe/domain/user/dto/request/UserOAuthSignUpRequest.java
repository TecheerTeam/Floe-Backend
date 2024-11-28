package project.floe.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOAuthSignUpRequest {

    @NotBlank(message = "nickname cannot be blank")
    private String nickname;

    private int experience;

    private int age;

    private String field;
}
