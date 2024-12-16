package project.floe.domain.user_follow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.user_follow.dto.response.GetUserFollowCountResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowStateResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowerListResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowingListResponse;
import project.floe.domain.user_follow.service.UserFollowService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "UserFollowController", description = "유저 팔로우 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserFollowController {

    private final UserFollowService userFollowService;

    @Operation(
            summary = "유저 팔로우 추가",
            description = "유저 팔로우 추가"
    )
    @PostMapping("{userId}/follow")
    public ResponseEntity<ResultResponse> addUserFollow(
            @PathVariable("userId") Long toUserId,
            HttpServletRequest request) {
        userFollowService.createUserFollow(toUserId,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultResponse.of(ResultCode.USER_FOLLOW_CREATE_SUCCESS));
    }
    @Operation(
            summary = "유저 팔로우 삭제",
            description = "유저 팔로우 삭제"
    )
    @DeleteMapping("{userId}/follow")
    public ResponseEntity<ResultResponse> deleteUserFollow(
            @PathVariable("userId") Long toUserId,
            HttpServletRequest request) {
        userFollowService.deleteUserFollow(toUserId,request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_FOLLOW_DELETE_SUCCESS));

    }
    @Operation(
            summary = "유저 팔로워 목록 조회",
            description = "유저 팔로워 목록 조회"
    )
    @GetMapping("{userId}/follow/follower")
    public ResponseEntity<ResultResponse> getUserFollowerList(
            @PathVariable("userId") Long userId,
            HttpServletRequest request) {
        GetUserFollowerListResponse dto = userFollowService.getUserFollowerList(userId,request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_FOLLOW_GET_SUCCESS, dto));

    }
    @Operation(
            summary = "유저 팔로잉 목록 조회",
            description = "유저 팔로잉 목록 조회"
    )
    @GetMapping("{userId}/follow/following")
    public ResponseEntity<ResultResponse> getUserFollowingList(
            @PathVariable("userId") Long userId,
            HttpServletRequest request) {
        GetUserFollowingListResponse dto = userFollowService.getUserFollowingList(userId,request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_FOLLOW_GET_SUCCESS, dto));

    }

    @Operation(
            summary = "유저 팔로우 수 조회",
            description = "유저 팔로우 수 조회"
    )
    @GetMapping("{userId}/follow/count")
    public ResponseEntity<ResultResponse> getUserFollowCount(
            @PathVariable("userId") Long userId) {
        GetUserFollowCountResponse dto = userFollowService.getUserFollowCount(userId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_FOLLOW_COUNT_GET_SUCCESS, dto));
    }
    @Operation(
            summary = "유저 팔로우 상태 조회",
            description = "유저 팔로우 상태 조회"
    )
    @GetMapping("{userId}/follow/status")
    public ResponseEntity<ResultResponse> getUserFollowStatus(
            @PathVariable("userId") Long toUserId,
            HttpServletRequest request) {
        GetUserFollowStateResponse dto = userFollowService.getUserFollowState(toUserId,request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_FOLLOW_STATUS_GET_SUCCESS, dto));
    }

}
