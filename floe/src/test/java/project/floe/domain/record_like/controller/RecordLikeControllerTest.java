package project.floe.domain.record_like.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.dto.response.GetRecordLikeListResponseDto;
import project.floe.domain.record_like.service.RecordLikeService;
import project.floe.domain.user.entity.User;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.ErrorResponse;
import project.floe.global.error.GlobalExceptionHandler;
import project.floe.global.error.exception.BusinessException;
import project.floe.global.error.exception.EmptyResultException;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RecordLikeControllerTest {
    @InjectMocks
    private RecordLikeController recordLikeController;
    @Mock
    private RecordLikeService recordLikeService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(recordLikeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void 좋아요수조회실패_존재하지않는기록() throws Exception {
        String url = "/api/v1/records/{recordId}/likes";
        Long pathVariable = 1L;
        ErrorResponse expectedResponse = ErrorResponse.of(ErrorCode.RECORD_NOT_FOUND_ERROR);
        doThrow(new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR))
                .when(recordLikeService).getRecordLikeCount(pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, pathVariable)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.businessCode").value(expectedResponse.getBusinessCode()))
                .andExpect(jsonPath("$.errorMessage").value(expectedResponse.getErrorMessage()));
    }

    @Test
    public void 좋아요수조회성공() throws Exception {
        String url = "/api/v1/records/{recordId}/likes";
        Long pathVariable = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_LIKE_COUNT_GET_SUCCESS);
        GetRecordLikeCountResponseDto expectedDto = new GetRecordLikeCountResponseDto(1L);
        doReturn(expectedDto).when(recordLikeService).getRecordLikeCount(pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, pathVariable)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.count").value(expectedDto.getCount()));
    }

    @Test
    public void 좋아요추가실패_중복() throws Exception {
        String url = "/api/v1/records/{recordId}/likes";
        Long pathVariable = 1L;
        Long userId = 1L;
        ErrorResponse expectedResponse = ErrorResponse.of(ErrorCode.RECORD_ALREADY_LIKED_ERROR);
        doThrow(new BusinessException(ErrorCode.RECORD_ALREADY_LIKED_ERROR))
                .when(recordLikeService).addRecordLike(userId, pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url, pathVariable)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.businessCode").value(expectedResponse.getBusinessCode()))
                .andExpect(jsonPath("$.errorMessage").value(expectedResponse.getErrorMessage()));
    }

    @Test
    public void 좋아요추가성공() throws Exception {
        String url = "/api/v1/records/{recordId}/likes";
        Long pathVariable = 1L;
        Long userId = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_LIKE_POST_SUCCESS);
        doNothing().when(recordLikeService).addRecordLike(userId, pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url, pathVariable)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }

    @Test
    public void 좋아요삭제실패_좋아요하지않았음() throws Exception {
        String url = "/api/v1/records/{recordId}/likes";
        Long pathVariable = 1L;
        Long userId = 1L;
        ErrorResponse expectedResponse = ErrorResponse.of(ErrorCode.RECORD_LIKE_NOT_FOUNT_ERROR);
        doThrow(new BusinessException(ErrorCode.RECORD_LIKE_NOT_FOUNT_ERROR))
                .when(recordLikeService).deleteRecordLike(userId, pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, pathVariable)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.businessCode").value(expectedResponse.getBusinessCode()))
                .andExpect(jsonPath("$.errorMessage").value(expectedResponse.getErrorMessage()));
    }

    @Test
    public void 좋아요한유저목록조회() throws Exception {
        String url = "/api/v1/records/{recordId}/like-list";
        Long pathVariable = 1L;
        List<User> userList = new ArrayList<>();
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_LIKE_LIST_GET_SUCCESS);
        GetRecordLikeListResponseDto expectedDto = new GetRecordLikeListResponseDto(userList);
        doReturn(expectedDto).when(recordLikeService).getRecordLikeList(pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, pathVariable)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.likeList").value(expectedDto.getLikeList()));
    }
}
