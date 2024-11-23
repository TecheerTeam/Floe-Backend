package project.floe.domain.record_like.controller;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.service.RecordLikeService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.ErrorResponse;
import project.floe.global.error.GlobalExceptionHandler;
import project.floe.global.error.exception.EmptyResultException;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@ExtendWith(MockitoExtension.class)
public class RecordLikeControllerTest {
    @InjectMocks
    private RecordLikeController recordLikeController;
    @Mock
    private RecordLikeService recordLikeService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(recordLikeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void 좋아요수조회실패_존재하지않는기록()throws Exception{
        String url = "/api/v1/records/{recordId}/likes";
        Long pathVariable = 0L;
        ErrorResponse expectedResponse = ErrorResponse.of(ErrorCode.RECORD_NOT_FOUND_ERROR);
        doThrow(new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR)).when(recordLikeService).getRecordLikeCount(pathVariable);

        mockMvc.perform(
                MockMvcRequestBuilders.get(url,pathVariable)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.businessCode").value(expectedResponse.getBusinessCode()))
                .andExpect(jsonPath("$.errorMessage").value(expectedResponse.getErrorMessage()));
    }

    @Test
    public void 좋아요수조회성공()throws Exception{
        String url = "/api/v1/records/{recordId}/likes";
        Long pathVariable = 0L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_LIKE_COUNT_GET_SUCCESS);
        GetRecordLikeCountResponseDto expectedDto = new GetRecordLikeCountResponseDto(1L);
        doReturn(new GetRecordLikeCountResponseDto(1L)).when(recordLikeService).getRecordLikeCount(pathVariable);

        mockMvc.perform(
                MockMvcRequestBuilders.get(url,pathVariable)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.count").value(expectedDto.getCount()));
    }
}
