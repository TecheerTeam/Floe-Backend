package project.floe.domain.user.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.floe.domain.user.entity.SocialType;
import project.floe.domain.user.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * 소셜 타입과 소셜의 식별값으로 회원을 찾는 메소드
     * 소셜 로그인으로 정보 제공을 동의한 순건 DB에 저장해야 하나, 아직 추가 정보(분야, 연차 등)을 입력받지 못함
     * 유저 객체는 DB에 있지만 추가 정보가 빠진 상태
     * 따라서 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원 찾기 위한 메소드
     **/
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    @Modifying
    @Query("UPDATE Record r SET r.isDeleted = true WHERE r.user.id = :userId")
    void softDeleteRecordsByUserId(@Param("userId")Long userId);

    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true WHERE c.user.id = :userId")
    void softDeleteCommentsByUserId(@Param("userId")Long userId);

    @Modifying
    @Query("DELETE RecordLike rl WHERE rl.user.id = :userId")
    void deleteRecordLikesByUserId(@Param("userId")Long userId);

    @Modifying
    @Query("DELETE RecordSave rs WHERE rs.user.id = :userId")
    void deleteRecordSavesByUserId(@Param("userId")Long userId);

    @Modifying
    @Query("DELETE CommentLike cl WHERE cl.user.id = :userId")
    void deleteCommentLikesByUserId(@Param("userId")Long userId);

    @Modifying
    @Query("DELETE UserFollow uf WHERE uf.fromUser.id = :userId OR uf.toUser.id = :userId")
    void deleteUserFollowsByUserId(@Param("userId")Long userId);
}
