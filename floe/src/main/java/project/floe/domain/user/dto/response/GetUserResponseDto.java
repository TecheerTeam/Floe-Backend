package project.floe.domain.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.floe.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
public class GetUserResponseDto {

    private String userId;
    private String name;
    private int experience;
    private int age;
    private String profileImage;
    private String field;

    public GetUserResponseDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getNickName();
        this.experience = user.getExperience();
        this.age = user.getAge();
        this.profileImage = user.getProfileImage();
        this.field = user.getField();
    }

}
