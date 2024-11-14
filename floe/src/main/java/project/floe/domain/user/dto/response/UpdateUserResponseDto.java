package project.floe.domain.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.floe.domain.user.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserResponseDto {
    private String name;
    private String email;
    private int experience;
    private int age;
    private String profileImage;
    private String field;

    public UpdateUserResponseDto(UserEntity userEntity){
        this.name = userEntity.getName();
        this.email = userEntity.getEmail();
        this.experience = userEntity.getExperience();
        this.age = userEntity.getAge();
        this.profileImage = userEntity.getProfileImage();
        this.field = userEntity.getField();
    }
}
