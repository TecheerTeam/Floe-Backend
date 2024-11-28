package project.floe.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "User userId cannot be blank")
    private String userId;

    @NotBlank(message = "User password cannot be blank")
    private String password;

    @NotBlank(message = "User name cannot be blank")
    private String name;

    @Email
    @NotBlank(message = "User email cannot be blank")
    private String email;

    @NotNull(message = "User experience cannot be blank")
    private Integer experience;

    @NotNull(message = "User age cannot be null")
    private Integer age;

    private String profileImage;

    @NotBlank(message = "User field cannot be blank")
    private String field;

}
