package project.floe.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import project.floe.domain.comment.dto.request.CreateCommentRequest;
import project.floe.domain.comment.dto.request.UpdateCommentRequest;
import project.floe.domain.comment.dto.response.GetCommentResponse;
import project.floe.domain.comment.dto.response.GetCommentUserResponse;
import project.floe.domain.comment.service.CommentService;
import project.floe.global.config.TestSecurityConfig;
import project.floe.global.result.ResultCode;

@WebMvcTest(CommentController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CreateCommentRequest createCommentRequest;
    private UpdateCommentRequest updateCommentRequest;
    private GetCommentResponse getCommentResponse;

    @BeforeEach
    void setUp() {
        createCommentRequest = CreateCommentRequest.builder()
                .recordId(1L)
                .content("테스트 댓글")
                .parentId(null)
                .build();

        updateCommentRequest = UpdateCommentRequest.builder()
                .content("수정된 댓글 내용")
                .build();

        getCommentResponse = GetCommentResponse.builder()
                .commentId(1L)
                .user(GetCommentUserResponse.builder()
                        .nickname("testUser")
                        .email("test@example.com")
                        .profileImage("test.jpg")
                        .build())
                .content("테스트 댓글")
                .build();
    }

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void 댓글생성_성공() throws Exception {
        doNothing().when(commentService).createComment(any(), any());

        mockMvc.perform(post("/api/v1/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResultCode.COMMENT_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.COMMENT_CREATE_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("댓글 페이징 조회 성공 테스트")
    void 댓글페이징조회_성공() throws Exception {
        Page<GetCommentResponse> mockResponse = new PageImpl<>(List.of(getCommentResponse));
        when(commentService.getCommentsByRecordId(eq(1L), any(Pageable.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/comments/1")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "updatedAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.COMMENT_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.COMMENT_GET_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("대댓글 페이징 조회 성공 테스트")
    void 대댓글페이징조회_성공() throws Exception {
        Page<GetCommentResponse> mockResponse = new PageImpl<>(List.of(getCommentResponse));
        when(commentService.getParentComment(eq(1L), any(Pageable.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/comments/1/replies")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "updatedAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.COMMENT_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.COMMENT_GET_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void 댓글수정_성공() throws Exception {
        doNothing().when(commentService).updateComment(eq(1L), any(UpdateCommentRequest.class));

        mockMvc.perform(put("/api/v1/comments/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.COMMENT_UPDATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.COMMENT_UPDATE_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void 댓글삭제_성공() throws Exception {
        doNothing().when(commentService).deleteComment(eq(1L));

        mockMvc.perform(delete("/api/v1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.COMMENT_DELETE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.COMMENT_DELETE_SUCCESS.getMessage()));
    }


}