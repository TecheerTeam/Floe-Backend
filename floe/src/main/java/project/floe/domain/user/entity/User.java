package project.floe.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.entity.BaseEntity;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // 사용자 아이디
    private String password; // 비밀번호
    private String nickName; // 별명
    private String email; // 사용자 이메일
    private String profileImage;
    private int experience; // 연차
    private int age;
    private String field;

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE, GITHUB

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = UserRole.USER;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    // 리프레시 토큰 재발급 메소드
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public static User from(UserSignUpRequest dto){
        return User.builder()
                .userId(dto.getUserId())
                .password(dto.getPassword())
                .nickName(dto.getNickName())
                .email(dto.getEmail())
                .experience(dto.getExperience())
                .age(dto.getAge())
                .field(dto.getField())
                .role(UserRole.USER)
                .build();
    }

    public void update(UserUpdateRequest dto){
        if(dto.getPassword() != null) this.password = dto.getPassword();
        if(dto.getNickName() != null)this.nickName = dto.getNickName();
        if(dto.getEmail() != null)this.email = dto.getEmail();
        if(dto.getExperience() != null)this.experience = dto.getExperience();
        if(dto.getAge() != null)this.age = dto.getAge();
        if(dto.getProfileImage() != null)this.profileImage = dto.getProfileImage();
        if(dto.getField() != null)this.field = dto.getField();
    }
}
