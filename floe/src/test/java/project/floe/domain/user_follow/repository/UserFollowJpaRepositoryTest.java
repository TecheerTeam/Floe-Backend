package project.floe.domain.user_follow.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.domain.user_follow.dto.response.GetUserFollowUserResponse;
import project.floe.domain.user_follow.entity.UserFollow;

@DataJpaTest
@ActiveProfiles("test")
class UserFollowJpaRepositoryTest {

    @Autowired
    private UserFollowJpaRepository userFollowJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자의 팔로워 목록 조회")
    void findFollowerListByToUserWithFollowState() {

        User userA = userRepository.save(User.builder()
                .email("userA@test.com")
                .nickname("UserA")
                .profileImage("profileA.png")
                .password("password")
                .build());

        User userB = userRepository.save(User.builder()
                .email("userB@test.com")
                .nickname("UserB")
                .profileImage("profileB.png")
                .password("password")
                .build());

        User myUser = userRepository.save(User.builder()
                .email("myUser@test.com")
                .nickname("MyUser")
                .profileImage("profileMyUser.png")
                .password("password")
                .build());

        userFollowJpaRepository.save(new UserFollow(userB, myUser)); // myUser -> userB
        userFollowJpaRepository.save(new UserFollow(userA, userB)); // UserB -> UserA

        List<GetUserFollowUserResponse> result = userFollowJpaRepository.findFollowerListByToUserWithFollowState(
                userA, myUser);

        assertThat(result).hasSize(1);
        GetUserFollowUserResponse response = result.get(0);
        assertThat(response.getUserId()).isEqualTo(userB.getId());
        assertThat(response.isFollowed()).isTrue();
    }

    @Test
    @DisplayName("사용자의 팔로잉 목록 조회")
    void findFollowingListByFromUserWithFollowState() {

        User userA = userRepository.save(User.builder()
                .email("userA@test.com")
                .nickname("UserA")
                .profileImage("profileA.png")
                .password("password")
                .build());

        User userB = userRepository.save(User.builder()
                .email("userB@test.com")
                .nickname("UserB")
                .profileImage("profileB.png")
                .password("password")
                .build());

        User myUser = userRepository.save(User.builder()
                .email("myUser@test.com")
                .nickname("MyUser")
                .profileImage("profileMyUser.png")
                .password("password")
                .build());

        userFollowJpaRepository.save(new UserFollow(userA, userB)); // userB -> userA
        userFollowJpaRepository.save(new UserFollow(myUser, userB)); // userB -> myUser

        List<GetUserFollowUserResponse> followings = userFollowJpaRepository.findFollowingListByFromUserWithFollowState(
                userB, myUser);

        assertThat(followings).hasSize(2);

        assertThat(followings.get(0).getUserId()).isEqualTo(userA.getId());
        assertThat(followings.get(0).isFollowed()).isFalse();
        assertThat(followings.get(1).getUserId()).isEqualTo(myUser.getId());
        assertThat(followings.get(1).isFollowed()).isFalse();
    }

    @Test
    @DisplayName("팔로우 상태 확인")
    void existsByToUserAndFromUser() {

        User fromUser = userRepository.save(User.builder()
                .email("fromUser@test.com")
                .nickname("FromUser")
                .profileImage("fromUser.png")
                .password("password")
                .build());

        User toUser = userRepository.save(User.builder()
                .email("toUser@test.com")
                .nickname("ToUser")
                .profileImage("toUser.png")
                .password("password")
                .build());

        userFollowJpaRepository.save(new UserFollow(toUser, fromUser));

        boolean result = userFollowJpaRepository.existsByToUserAndFromUser(toUser, fromUser);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("팔로우 삭제 테스트")
    void deleteByToUserAndFromUser() {
        // Given
        User fromUser = userRepository.save(User.builder()
                .email("fromUser@test.com")
                .nickname("FromUser")
                .profileImage("fromUser.png")
                .password("password")
                .build());

        User toUser = userRepository.save(User.builder()
                .email("toUser@test.com")
                .nickname("ToUser")
                .profileImage("toUser.png")
                .password("password")
                .build());

        userFollowJpaRepository.save(new UserFollow(toUser, fromUser));

        int deletedCount = userFollowJpaRepository.deleteByToUserAndFromUser(toUser, fromUser);
        boolean exists = userFollowJpaRepository.existsByToUserAndFromUser(toUser, fromUser);

        assertThat(deletedCount).isEqualTo(1);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("팔로워 및 팔로잉 수 조회")
    void countByToUserAndCountByFromUser() {

        User userA = userRepository.save(User.builder()
                .email("userA@test.com")
                .nickname("UserA")
                .profileImage("profileA.png")
                .password("password")
                .build());

        User userB = userRepository.save(User.builder()
                .email("userB@test.com")
                .nickname("UserB")
                .profileImage("profileB.png")
                .password("password")
                .build());

        User userC = userRepository.save(User.builder()
                .email("userC@test.com")
                .nickname("UserC")
                .profileImage("profileC.png")
                .password("password")
                .build());

        userFollowJpaRepository.save(new UserFollow(userB, userA));
        userFollowJpaRepository.save(new UserFollow(userC, userA));
        userFollowJpaRepository.save(new UserFollow(userA, userB));

        long followerCount = userFollowJpaRepository.countByToUser(userA);
        long followingCount = userFollowJpaRepository.countByFromUser(userA);

        assertThat(followerCount).isEqualTo(1);
        assertThat(followingCount).isEqualTo(2);
    }
}