package project.floe.domain.user_follow.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import project.floe.domain.user_follow.dto.response.GetUserFollowCountResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowStateResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowUserResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowerListResponse;
import project.floe.domain.user_follow.dto.response.GetUserFollowingListResponse;
import project.floe.domain.user_follow.service.UserFollowService;
import project.floe.global.config.TestSecurityConfig;
import project.floe.global.result.ResultCode;

@WebMvcTest(UserFollowController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UserFollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFollowService userFollowService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 팔로우 추가")
    void addUserFollow_Success() throws Exception {

        doNothing().when(userFollowService).createUserFollow(any(), any());

        mockMvc.perform(post("/api/v1/users/1/follow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_FOLLOW_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_FOLLOW_CREATE_SUCCESS.getMessage()));

        verify(userFollowService, times(1)).createUserFollow(any(), any());
    }

    @Test
    @DisplayName("유저 팔로우 삭제")
    void deleteUserFollow_Success() throws Exception {

        doNothing().when(userFollowService).deleteUserFollow(any(), any());

        mockMvc.perform(delete("/api/v1/users/1/follow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_FOLLOW_DELETE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_FOLLOW_DELETE_SUCCESS.getMessage()));

        verify(userFollowService, times(1)).deleteUserFollow(any(), any());
    }

    @Test
    @DisplayName("유저 팔로우 상태 조회")
    void getUserFollowState_Success() throws Exception {

        GetUserFollowStateResponse response = new GetUserFollowStateResponse(true);

        when(userFollowService.getUserFollowState(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1/follow/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_FOLLOW_STATUS_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_FOLLOW_STATUS_GET_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.isFollowed").value(true));

        verify(userFollowService, times(1)).getUserFollowState(any(), any());
    }

    @Test
    @DisplayName("유저 팔로우 수 조회")
    void getUserFollowCount_Success() throws Exception {
        GetUserFollowCountResponse response = new GetUserFollowCountResponse(10, 5);

        when(userFollowService.getUserFollowCount(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1/follow/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_FOLLOW_COUNT_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_FOLLOW_COUNT_GET_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.followerCount").value(5))
                .andExpect(jsonPath("$.data.followingCount").value(10));

        verify(userFollowService, times(1)).getUserFollowCount(any());
    }

    @Test
    @DisplayName("유저 팔로워 목록 조회")
    void getUserFollowerList_Success() throws Exception {
        Long userId = 1L;

        List<GetUserFollowUserResponse> followerList = List.of(
                new GetUserFollowUserResponse(2L, "test1", "image1.jpg", true),
                new GetUserFollowUserResponse(3L, "test2", "image2.jpg", false)
        );
        GetUserFollowerListResponse response = new GetUserFollowerListResponse(followerList);

        when(userFollowService.getUserFollowerList(eq(userId), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1/follow/follower")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_FOLLOW_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_FOLLOW_GET_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.userFollowerList[0].userId").value(2))
                .andExpect(jsonPath("$.data.userFollowerList[0].nickName").value("test1"))
                .andExpect(jsonPath("$.data.userFollowerList[0].profileImage").value("image1.jpg"))
                .andExpect(jsonPath("$.data.userFollowerList[0].isFollowed").value(true))
                .andExpect(jsonPath("$.data.userFollowerList[1].userId").value(3))
                .andExpect(jsonPath("$.data.userFollowerList[1].isFollowed").value(false))
                .andExpect(jsonPath("$.data.userFollowerList[1].profileImage").value("image2.jpg"))
                .andExpect(jsonPath("$.data.userFollowerList[1].nickName").value("test2"));

        verify(userFollowService, times(1)).getUserFollowerList(any(), any());
    }

    @Test
    @DisplayName("유저 팔로잉 목록 조회")
    void getUserFollowingList_Success() throws Exception {

        // Mock Response Data
        List<GetUserFollowUserResponse> followingList = List.of(
                new GetUserFollowUserResponse(4L, "test3", "image3.jpg", false),
                new GetUserFollowUserResponse(5L, "test4", "image4.jpg", true)
        );
        GetUserFollowingListResponse response = new GetUserFollowingListResponse(followingList);

        // Mock Service 호출
        when(userFollowService.getUserFollowingList(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1/follow/following")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_FOLLOW_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_FOLLOW_GET_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.userFollowingList[0].userId").value(4))
                .andExpect(jsonPath("$.data.userFollowingList[0].nickName").value("test3"))
                .andExpect(jsonPath("$.data.userFollowingList[0].profileImage").value("image3.jpg"))
                .andExpect(jsonPath("$.data.userFollowingList[0].isFollowed").value(false))
                .andExpect(jsonPath("$.data.userFollowingList[1].userId").value(5))
                .andExpect(jsonPath("$.data.userFollowingList[1].isFollowed").value(true))
                .andExpect(jsonPath("$.data.userFollowingList[1].profileImage").value("image4.jpg"))
                .andExpect(jsonPath("$.data.userFollowingList[1].nickName").value("test4"));

        verify(userFollowService, times(1)).getUserFollowingList(any(), any());
    }
}