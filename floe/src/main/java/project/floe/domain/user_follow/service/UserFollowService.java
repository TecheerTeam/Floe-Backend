package project.floe.domain.user_follow.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class UserFollowService {
    private final UserFollowJpaRepository userFollowJpaRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional
    public void createUserFollow(Long toUserId, HttpServletRequest request) {
        User fromUser = getAuthenticatedUser(request);
        User toUser = getUserByUserId(toUserId);
        
        if (toUser.equals(fromUser)) {
            throw new UserServiceException(ErrorCode.USER_FOLLOW_SELF_ERROR);
        }
        try {
            userFollowJpaRepository.save(new UserFollow(toUser, fromUser));
        } catch (DataIntegrityViolationException e) {
            throw new UserServiceException(ErrorCode.USER_FOLLOW_ALREADY_EXIST_ERROR);
        }
    }

    @Transactional
    public void deleteUserFollow(Long toUserId, HttpServletRequest request) {
        User toUser = getUserByUserId(toUserId);

        User fromUser = getAuthenticatedUser(request);

        if (userFollowJpaRepository.deleteByToUserAndFromUser(toUser, fromUser) == 0) {
            throw new UserServiceException(ErrorCode.USER_FOLLOW_NOT_FOUND_ERROR);
        }
    }

    public GetUserFollowStateResponse getUserFollowState(Long toUserId, HttpServletRequest request) {
        User toUser = getUserByUserId(toUserId);
        User fromUser = getAuthenticatedUser(request);

        boolean isFollowed = userFollowJpaRepository.existsByToUserAndFromUser(toUser, fromUser);

        return new GetUserFollowStateResponse(isFollowed);
    }

    public GetUserFollowCountResponse getUserFollowCount(Long userId) {
        User user = getUserByUserId(userId);

        long followingCount = userFollowJpaRepository.countByFromUser(user);
        long followerCount = userFollowJpaRepository.countByToUser(user);
        return new GetUserFollowCountResponse(followingCount, followerCount);
    }

    public GetUserFollowerListResponse getUserFollowerList(Long toUserId, HttpServletRequest request) {

        User myUser = getAuthenticatedUser(request);

        User toUser = getUserByUserId(toUserId);
        List<GetUserFollowUserResponse> followers = userFollowJpaRepository.findFollowerListByToUserWithFollowState(
                toUser, myUser);
        return new GetUserFollowerListResponse(followers);
    }

    public GetUserFollowingListResponse getUserFollowingList(Long fromUserId, HttpServletRequest request) {

        User myUser = getAuthenticatedUser(request);

        User fromUser = getUserByUserId(fromUserId);

        List<GetUserFollowUserResponse> followings = userFollowJpaRepository.findFollowingListByFromUserWithFollowState(
                fromUser, myUser);

        return new GetUserFollowingListResponse(followings);
    }

    public User getAuthenticatedUser(HttpServletRequest request) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));
        return findUser;
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));
    }

}
