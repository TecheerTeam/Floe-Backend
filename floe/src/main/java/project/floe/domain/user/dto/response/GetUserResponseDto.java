package project.floe.domain.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.floe.domain.user.entity.UserEntity;

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

    public GetUserResponseDto(UserEntity userEntity) {
        this.userId = userEntity.getUserId();
        this.name = userEntity.getName();
        this.experience = userEntity.getExperience();
        this.age = userEntity.getAge();
        this.profileImage = userEntity.getProfileImage();
        this.field = userEntity.getField();
    }

}
