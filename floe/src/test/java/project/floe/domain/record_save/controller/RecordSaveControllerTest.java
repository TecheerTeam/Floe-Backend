package project.floe.domain.record_save.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.floe.domain.record_save.dto.response.GetCheckSavedRecordResponseDto;
import project.floe.domain.record_save.dto.response.GetSaveCountResponseDto;
import project.floe.domain.record_save.dto.response.GetSaveRecordsResponseDto;
import project.floe.domain.record_save.service.RecordSaveService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RecordSaveControllerTest {

    @InjectMocks
    private RecordSaveController recordSaveController;

    @Mock
    private RecordSaveService recordSaveService;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(recordSaveController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void 기록저장() throws Exception {
        String url = "/api/v1/records/{recordId}/save";
        Long pathValuable = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_SAVE_POST_SUCCESS);
        doNothing().when(recordSaveService).addRecordSave(eq(pathValuable), any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url, pathValuable)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));

    }

    @Test
    void 기록저장삭제() throws Exception {
        String url = "/api/v1/records/{recordId}/save";
        Long pathValuable = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_SAVE_DELETE_SUCCESS);
        doNothing().when(recordSaveService).deleteRecordSave(eq(pathValuable), any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, pathValuable)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }

    @Test
    void 기록저장개수조회() throws Exception {
        String url = "/api/v1/records/{recordId}/save-count";
        Long pathValuable = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_SAVE_COUNT_GET_SUCCESS);
        GetSaveCountResponseDto expectedDto = new GetSaveCountResponseDto(0L);
        doReturn(expectedDto).when(recordSaveService).getSaveCountByRecordId(pathValuable);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, pathValuable)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.count").value(expectedDto.getCount()));
    }

    @Test
    void 저장한기록목록조회() throws Exception {
        String url = "/api/v1/users/save/record-list";
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_SAVE_LIST_GET_SUCCESS);
        Page<GetSaveRecordsResponseDto> expectedDto = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 5), 0);
        doReturn(expectedDto).when(recordSaveService)
                .getSaveRecordList(any(Pageable.class), any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url)
                                .param("page", "0")
                                .param("size", "5")
                                .param("sort", "createdAt,desc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }

    @Test
    void 기록저장여부확인() throws Exception {
        String url = "/api/v1/records/{recordId}/save";
        Long pathValuable = 1L;
        GetCheckSavedRecordResponseDto expectedDto = new GetCheckSavedRecordResponseDto(true);
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_SAVE_CHECK_SUCCESS);

        doReturn(expectedDto).when(recordSaveService).checkSavedRecord(any(Long.class),any(HttpServletRequest.class));

        mockMvc.perform(
                MockMvcRequestBuilders.get(url,pathValuable)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.saved").value(expectedDto.isSaved()));
    }

}
