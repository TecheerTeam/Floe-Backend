package project.floe.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserOAuthSignUpRequest {

    private String nickname;
    private int experience;
    private int age;
    private String field;
}
