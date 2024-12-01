package project.floe.domain.record_save.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.floe.domain.record_save.service.RecordSaveService;
import project.floe.global.error.GlobalExceptionHandler;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@ExtendWith(MockitoExtension.class)
public class RecordSaveControllerTest {
    @InjectMocks
    private RecordSaveController recordSaveController;

    @Mock
    private RecordSaveService recordSaveService;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(recordSaveController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void 기록저장() throws Exception {
        String url = "/api/v1/records/{recordId}/save";
        Long pathValuable = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_SAVE_POST_SUCCESS);
        doNothing().when(recordSaveService).addRecordSave(eq(pathValuable),any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url, pathValuable)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));

    }

    @Test
    void 기록삭제() throws Exception {
        String url = "/api/v1/records/{recordId}/save";
        Long pathValuable = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.RECORD_SAVE_DELETE_SUCCESS);
        doNothing().when(recordSaveService).deleteRecordSave(eq(pathValuable),any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, pathValuable)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }
}
