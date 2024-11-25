package project.floe.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String password;
    private String nickName;
    private String email;
    private Integer experience;
    private Integer age;
    private String profileImage;
    private String field;
}
