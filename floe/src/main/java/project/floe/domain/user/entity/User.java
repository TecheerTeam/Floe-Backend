package project.floe.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.floe.domain.user.dto.request.SignUpRequestDto;
import project.floe.domain.user.dto.request.UpdateUserRequestDto;
import project.floe.entity.BaseEntity;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false)
    private Integer age;

    private String profileImage;

    @Column(nullable = false)
    private String field;

    public User(SignUpRequestDto dto){
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
