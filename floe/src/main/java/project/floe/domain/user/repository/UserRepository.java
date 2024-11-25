package project.floe.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.floe.domain.user.entity.SocialType;
import project.floe.domain.user.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickName(String nickname);
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * 소셜 타입과 소셜의 식별값으로 회원을 찾는 메소드
     * 소셜 로그인으로 정보 제공을 동의한 순건 DB에 저장해야 하나, 아직 추가 정보(분야, 연차 등)을 입력받지 못함
     * 유저 객체는 DB에 있지만 추가 정보가 빠진 상태
     * 따라서 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원 찾기 위한 메소드
    **/
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
