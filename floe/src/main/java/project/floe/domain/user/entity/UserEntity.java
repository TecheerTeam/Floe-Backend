package project.floe.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.floe.domain.user.dto.request.SignUpRequestDto;
import project.floe.domain.user.dto.request.UpdateUserRequestDto;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userId;
    private String password;
    private String name;
    private String email;
    private int experience;
    private int age;
    private String profileImage;
    private String field;

    public UserEntity(SignUpRequestDto dto){
        this.userId = dto.getUserId();
        this.password = dto.getPassword();
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.experience = dto.getExperience();
        this.age = dto.getAge();
        this.profileImage = dto.getProfileImage();
        this.field = dto.getField();
    }

    public void update(UpdateUserRequestDto dto){
        if(dto.getPassword() != null) this.password = dto.getPassword();
        if(dto.getName() != null)this.name = dto.getName();
        if(dto.getEmail() != null)this.email = dto.getEmail();
        if(dto.getExperience() != null)this.experience = dto.getExperience();
        if(dto.getAge() != null)this.age = dto.getAge();
        if(dto.getProfileImage() != null)this.profileImage = dto.getProfileImage();
        if(dto.getField() != null)this.field = dto.getField();
    }
}