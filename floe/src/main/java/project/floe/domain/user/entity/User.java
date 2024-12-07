package project.floe.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.floe.domain.user.dto.request.UserOAuthSignUpRequest;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.entity.BaseEntity;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@AllArgsConstructor
@SQLRestriction("is_deleted = false")
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE user_id = ?")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email; // 사용자 이메일

    @Column(name = "password", nullable = false)
    private String password; // 비밀번호

    @Column(name = "nickname", nullable = true)
    private String nickname; // 별명

    @Column(name = "profile_image", nullable = true)
    private String profileImage;

    @Column(name = "experience", nullable = true)
    private int experience; // 연차

    @Column(name = "age", nullable = true)
    private int age;

    @Column(name = "field", nullable = true)
    private String field;

    @Column(name = "social_id", nullable = true)
    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    @Column(name = "refresh_token", nullable = true)
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
    public void passwordEncode(BCryptPasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    // 리프레시 토큰 재발급 메소드
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public static User from(UserSignUpRequest dto) {
        return User.builder()
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .experience(dto.getExperience())
                .age(dto.getAge())
                .field(dto.getField())
                .role(UserRole.USER)
                .build();
    }

    public void updateProfileImage(String updatedUrl) {
        this.profileImage = updatedUrl;
    }

    public void update(UserUpdateRequest dto) {
        if (dto.getPassword() != null) {
            this.password = dto.getPassword();
        }
        if (dto.getNickname() != null) {
            this.nickname = dto.getNickname();
        }
        if (dto.getExperience() != null) {
            this.experience = dto.getExperience();
        }
        if (dto.getAge() != null) {
            this.age = dto.getAge();
        }
        if (dto.getField() != null) {
            this.field = dto.getField();
        }
    }

    public void oAuthSignUp(UserOAuthSignUpRequest dto) {
        this.nickname = dto.getNickname();
        this.experience = dto.getExperience();
        this.age = dto.getAge();
        this.field = dto.getField();
        authorizeUser();
    }
}
