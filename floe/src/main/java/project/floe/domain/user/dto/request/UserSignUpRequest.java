package project.floe.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor

// 자체 로그인에 사용되는 dto
public class UserSignUpRequest {
    private String userId;
    private String password;
    private String nickName;
    private String email;
    private int experience;
    private int age;
    private String profileImage;
    private String field;

}
