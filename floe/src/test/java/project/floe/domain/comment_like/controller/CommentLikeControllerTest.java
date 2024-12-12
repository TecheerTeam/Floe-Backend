package project.floe.domain.comment_like.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import project.floe.domain.comment_like.dto.response.GetCommentLikeCountResponse;
import project.floe.domain.comment_like.dto.response.GetCommentLikeUserListResponse;
import project.floe.domain.comment_like.service.CommentLikeService;
import project.floe.global.config.TestSecurityConfig;
import project.floe.global.result.ResultCode;

@WebMvcTest(CommentLikeController.class) // Controller 단위 테스트
@ActiveProfiles("test") // 테스트 프로파일 설정
@Import(TestSecurityConfig.class)
class CommentLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentLikeService commentLikeService;

    @Autowired
    private ObjectMapper objectMapper; // JSON 직렬화/역직렬화

    @DisplayName("댓글 좋아요 추가")
    @Test
    void addCommentLike() throws Exception {
        // Service 메서드 Mock 설정
        doNothing().when(commentLikeService).createCommentLike(any(), any());

        mockMvc.perform(post("/api/v1/comments/1/likes")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResultCode.Comment_LIKE_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.Comment_LIKE_CREATE_SUCCESS.getMessage()));
    }

    @DisplayName("댓글 좋아요 삭제")
    @Test
    void deleteCommentLike() throws Exception {
        // Service 메서드 Mock 설정
        doNothing().when(commentLikeService).deleteCommentLike(any(), any());

        mockMvc.perform(delete("/api/v1/comments/1/likes")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.Comment_LIKE_DELETE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.Comment_LIKE_DELETE_SUCCESS.getMessage()));
    }

    @DisplayName("댓글 좋아요 개수 조회")
    @Test
    void getCommentLikeCount() throws Exception {
        // Given
        GetCommentLikeCountResponse mockResponse = new GetCommentLikeCountResponse(1L);
        when(commentLikeService.getCommentLikeCount(any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/comments/1/likes/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.Comment_LIKE_COUNT_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @DisplayName("댓글 좋아요 유저 목록 조회")
    @Test
    void getCommentLikeUsers() throws Exception {
        // Given
        GetCommentLikeUserListResponse mockResponse = new GetCommentLikeUserListResponse(List.of());
        when(commentLikeService.getCommentLikeUsers(any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/comments/1/likes/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.Comment_LIKE_USERS_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.commentLikeUsers").isArray());
    }
}