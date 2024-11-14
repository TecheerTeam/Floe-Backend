package project.floe.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequestDto {
    private String password;
    private String name;
    private String email;
    private int experience;
    private int age;
    private String profileImage;
    private String field;
}
