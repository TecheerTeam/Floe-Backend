package project.floe.domain.user_follow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.domain.user_follow.dto.response.GetUserFollowCountResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowStateResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowUserResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowerListResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowingListResponse;
import project.floe.domain.user_follow.entity.UserFollow;
import project.floe.domain.user_follow.repository.UserFollowJpaRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@ExtendWith(MockitoExtension.class)
class UserFollowServiceTest {

    @Mock
    private UserFollowJpaRepository userFollowJpaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserFollowService userFollowService;

    private User userA;
    private User userB;
    private String email;

    @BeforeEach
    void setUp() {
        email = "test@example.com";

        userA = User.builder()
                .id(1L)
                .email(email)
                .nickname("UserA")
                .profileImage("profileA.png")
                .password("password")
                .build();

        userB = User.builder()
                .id(2L)
                .email("userB@test.com")
                .nickname("UserB")
                .profileImage("profileB.png")
                .password("password")
                .build();
    }

    @Test
    @DisplayName("팔로우 생성 ")
    void createUserFollow_success() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));

        userFollowService.createUserFollow(userB.getId(), mock(HttpServletRequest.class));

        verify(userFollowJpaRepository, times(1)).save(any(UserFollow.class));
    }

    @Test
    @DisplayName("팔로우 생성 실패 자기 자신 팔로우 예외")
    void createUserFollow_selfFollow_throwsException() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userA.getId())).thenReturn(Optional.of(userA));

        UserServiceException exception = assertThrows(UserServiceException.class, () ->
                userFollowService.createUserFollow(userA.getId(), mock(HttpServletRequest.class)));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_FOLLOW_SELF_ERROR);
    }

    @Test
    @DisplayName("팔로우 생성 실패 이미 존재하는 팔로우")
    void createUserFollow_alreadyExists_throwsException() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.save(any(UserFollow.class))).thenThrow(
                new UserServiceException(ErrorCode.USER_FOLLOW_ALREADY_EXIST_ERROR));

        UserServiceException exception = assertThrows(UserServiceException.class, () ->
                userFollowService.createUserFollow(userB.getId(), mock(HttpServletRequest.class)));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_FOLLOW_ALREADY_EXIST_ERROR);
    }

    @Test
    @DisplayName(" 팔로우 삭제 ")
    void deleteUserFollow_success() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.deleteByToUserAndFromUser(userB, userA)).thenReturn(1);

        userFollowService.deleteUserFollow(userB.getId(), mock(HttpServletRequest.class));

        verify(userFollowJpaRepository, times(1)).deleteByToUserAndFromUser(userB, userA);
    }

    @Test
    @DisplayName("팔로우 삭제 실패 존재하지 않는 팔로우")
    void deleteUserFollow_notFound_throwsException() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.deleteByToUserAndFromUser(userB, userA)).thenReturn(0);

        UserServiceException exception = assertThrows(UserServiceException.class, () ->
                userFollowService.deleteUserFollow(userB.getId(), mock(HttpServletRequest.class)));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_FOLLOW_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("팔로우 상태 확인")
    void getUserFollowState() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.existsByToUserAndFromUser(userB, userA)).thenReturn(true);

        GetUserFollowStateResponse response = userFollowService.getUserFollowState(userB.getId(),
                mock(HttpServletRequest.class));

        assertThat(response.isFollowed()).isTrue();
    }

    @Test
    @DisplayName("팔로우 수 조회")
    void getUserFollowCount() {
        when(userRepository.findById(userA.getId())).thenReturn(Optional.of(userA));
        when(userFollowJpaRepository.countByFromUser(userA)).thenReturn(10L);
        when(userFollowJpaRepository.countByToUser(userA)).thenReturn(20L);

        GetUserFollowCountResponse response = userFollowService.getUserFollowCount(userA.getId());

        assertThat(response.getFollowingCount()).isEqualTo(10L);
        assertThat(response.getFollowerCount()).isEqualTo(20L);
    }

    @Test
    @DisplayName("팔로워 목록 조회")
    void getUserFollowerList() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));

        List<GetUserFollowUserResponse> followers = List.of(
                new GetUserFollowUserResponse(1L, "User1", "profile1.png", true));

        when(userFollowJpaRepository.findFollowerListByToUserWithFollowState(userB, userA)).thenReturn(followers);

        GetUserFollowerListResponse response = userFollowService.getUserFollowerList(userB.getId(),
                mock(HttpServletRequest.class));

        assertThat(response.userFollowerList()).hasSize(1);
        assertThat(response.userFollowerList().get(0).getNickName()).isEqualTo("User1");
    }

    @Test
    @DisplayName("팔로잉 목록 조회")
    void getUserFollowingList() {
        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));

        List<GetUserFollowUserResponse> followings = List.of(
                new GetUserFollowUserResponse(2L, "UserB", "profileB.png", true));

        when(userFollowJpaRepository.findFollowingListByFromUserWithFollowState(userB, userA))
                .thenReturn(followings);

        GetUserFollowingListResponse response = userFollowService.getUserFollowingList(userB.getId(),
                mock(HttpServletRequest.class));

        assertThat(response.userFollowingList()).hasSize(1);
        assertThat(response.userFollowingList().get(0).getNickName()).isEqualTo("UserB");
    }
}