package project.floe.domain.user_follow.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user_follow.dto.response.GetUserFollowUserResponse;
import project.floe.domain.user_follow.entity.UserFollow;

@Repository
public interface UserFollowJpaRepository extends JpaRepository<UserFollow, Long> {
    long countByToUser(User toUser);
    long countByFromUser(User fromUser);

    int deleteByToUserAndFromUser(User toUser, User fromUser);

    boolean existsByToUserAndFromUser(User toUser, User fromUser);
    /**
     * 특정 사용자의 팔로워 목록을 조회합니다.
     * 각 팔로워에 대해 현재 로그인한 사용자가 해당 팔로워를 다시 팔로우했는지 여부를 함께 반환합니다.
     *
     * SELECT:
     * - u.id: 팔로워의 사용자 ID
     * - u.nickname: 팔로워의 닉네임
     * - u.profileImage: 팔로워의 프로필 이미지
     * - CASE WHEN uf2.id IS NOT NULL THEN true ELSE false END:
     *     현재 로그인한 사용자가 해당 팔로워를 팔로우했는지 여부를 반환합니다.
     *
     * FROM:
     * - UserFollow uf: 특정 사용자를 팔로우하는 관계 테이블을 기준으로 가져옵니다.
     *
     * JOIN:
     * - uf.fromUser u: 팔로워 사용자의 정보를 가져옵니다.
     *
     * LEFT JOIN:
     * - UserFollow uf2: 현재 로그인한 사용자의 팔로우 관계를 확인합니다.
     *   조건:
     *   - uf2.toUser = u (팔로워 대상이 팔로워 사용자임)
     *   - uf2.fromUser = :myUser (현재 로그인한 사용자의 팔로우 상태)
     *
     * WHERE:
     * - uf.toUser = :toUser → 특정 사용자를 팔로우하는 사용자 목록을 조회합니다.
     */
    @Query("SELECT new project.floe.domain.user_follow.dto.response.GetUserFollowUserResponse( " +
            "u.id, u.nickname, u.profileImage, " +
            "CASE WHEN uf2.id IS NOT NULL THEN true ELSE false END ) " +
            "FROM UserFollow uf " +
            "JOIN uf.fromUser u " +
            "LEFT JOIN UserFollow uf2 ON uf2.toUser = u AND uf2.fromUser = :myUser " +
            "WHERE uf.toUser = :toUser")
    List<GetUserFollowUserResponse> findFollowerListByToUserWithFollowState(
            @Param("toUser") User toUser,
            @Param("myUser") User myUser);

    /**
     * 특정 사용자가 팔로우한 대상 목록(팔로잉 목록)을 조회합니다.
     * 각 팔로우 대상에 대해 현재 로그인한 사용자가 해당 대상을 다시 팔로우했는지 여부를 함께 반환합니다.
     *
     * SELECT:
     * - u.id: 팔로우 대상의 사용자 ID
     * - u.nickname: 팔로우 대상의 닉네임
     * - u.profileImage: 팔로우 대상의 프로필 이미지
     * - CASE WHEN uf2.id IS NOT NULL THEN true ELSE false END:
     *     현재 로그인한 사용자가 해당 대상을 팔로우했는지 여부를 반환합니다.
     *
     * FROM:
     * - UserFollow uf: 특정 사용자의 팔로우 관계를 기준으로 가져옵니다.
     *
     * JOIN:
     * - uf.toUser u: 팔로우 대상 사용자의 정보를 가져옵니다.
     *
     * LEFT JOIN:
     * - UserFollow uf2: 현재 로그인한 사용자의 팔로우 관계를 확인합니다.
     *   조건:
     *   - uf2.toUser = u (팔로우 대상이 해당 사용자임)
     *   - uf2.fromUser = :myUser (현재 로그인한 사용자의 팔로우 상태)
     *
     * WHERE:
     * - uf.fromUser = :fromUser → 특정 사용자가 팔로우한 사용자 목록을 조회합니다.
     */
    @Query("SELECT new project.floe.domain.user_follow.dto.response.GetUserFollowUserResponse( " +
            "u.id, u.nickname, u.profileImage, " +
            "CASE WHEN uf2.id IS NOT NULL THEN true ELSE false END ) " +
            "FROM UserFollow uf " +
            "JOIN uf.toUser u " +
            "LEFT JOIN UserFollow uf2 ON uf2.toUser = u AND uf2.fromUser = :myUser " +
            "WHERE uf.fromUser = :fromUser")
    List<GetUserFollowUserResponse> findFollowingListByFromUserWithFollowState(
            @Param("fromUser") User fromUser,
            @Param("myUser") User myUser);
}
