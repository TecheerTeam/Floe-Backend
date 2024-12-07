package project.floe.domain.record.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.dto.response.UpdateRecordResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.user.entity.User;
import project.floe.global.config.TestSecurityConfig;
import project.floe.global.result.ResultCode;

@WebMvcTest(RecordController.class)
@Import(TestSecurityConfig.class)
class RecordControllerTest {

    public static final String BASE_PATH = "/api/v1/records";

    @MockBean
    private RecordService recordService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext applicationContext) {
        mockMvc =
                MockMvcBuilders.webAppContextSetup(applicationContext)
                        .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                        .build();
    }

    @Test
    @DisplayName("기록 생성 테스트")
    void createRecord() throws Exception {
        CreateRecordRequest dto = CreateRecordRequest.builder()
                .title("test")
                .content("test")
                .recordType(RecordType.FLOE)
                .build();
        Long recordId = 1L;
        // Mocking MultipartFile for files
        MockMultipartFile file1 = new MockMultipartFile("files", "file1.jpg", "image/jpeg", "file1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.jpg", "image/jpeg", "file2 content".getBytes());

        // Mocking the dto as a part
        MockMultipartFile dtoFile = new MockMultipartFile("dto", "", "application/json",
                new ObjectMapper().writeValueAsString(dto).getBytes());

        when(recordService.createRecord(any(), any(), any())).thenReturn(recordId);

        // MockMvc를 통해 POST 요청을 수행
        mockMvc.perform(multipart(BASE_PATH)
                        .file(file1)   // 첫 번째 파일
                        .file(file2)   // 두 번째 파일
                        .file(dtoFile)  // DTO를 JSON 형식으로 전송
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResultCode.RECORD_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.RECORD_CREATE_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("기록 수정 테스트")
    void updateRecord() throws Exception {
        // Given: 수정할 데이터 준비
        Long recordId = 1L;
        UpdateRecordRequest updateDto = UpdateRecordRequest.builder()
                .title("Updated title")
                .content("Updated content")
                .recordType(RecordType.FLOE)
                .build();

        // 파일 생성 (MockMultipartFile 사용)
        MockMultipartFile file1 = new MockMultipartFile("updateFiles", "file1.jpg", "image/jpeg",
                "file1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("updateFiles", "file2.jpg", "image/jpeg",
                "file2 content".getBytes());

        // updateDto를 JSON 형식으로 MockMultipartFile로 변환
        MockMultipartFile dtoFile = new MockMultipartFile("updateDto", "", "application/json",
                new ObjectMapper().writeValueAsString(updateDto).getBytes());

        // 수정된 기록의 응답 (가정한 수정된 기록)
        Record modifiedRecord = Record.builder()
                .id(recordId)
                .user(User.builder().email("test@example.com").password("1234").nickname("test").build())
                .title("Updated title")
                .content("Updated content")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();
        UpdateRecordResponse response = UpdateRecordResponse.from(modifiedRecord);

        // Service mock 설정
        when(recordService.modifyRecord(eq(recordId), any(UpdateRecordRequest.class), anyList()))
                .thenReturn(modifiedRecord);

        // Then: PUT 요청을 통해 테스트
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_PATH + "/{recordId}", recordId)  // URL에 recordId 포함
                        .file(file1)   // 첫 번째 파일
                        .file(file2)   // 두 번째 파일
                        .file(dtoFile)  // DTO JSON 파일
                        .with(request -> {
                            request.setMethod("PUT"); // multipart에서는 기본이 POST이므로 PUT으로 변경
                            return request;
                        })
                        .accept(MediaType.APPLICATION_JSON))  // 응답 타입 설정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RECORD_MODIFY_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.RECORD_MODIFY_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()))
                .andExpect(jsonPath("$.data.content").value(response.getContent()));
    }

    @Test
    @DisplayName("기록 삭제 테스트")
    void deleteRecord() throws Exception {
        doNothing().when(recordService).deleteRecord(1L);

        mockMvc.perform(delete(BASE_PATH + "/{recordId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RECORD_DELETE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.RECORD_DELETE_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("단건 조회 테스트")
    void getDetailRecord() throws Exception {
        Record record = Record.builder()
                .user(User.builder().email("test@example.com").password("1234").nickname("test").build())
                .title("testRecord")
                .recordType(RecordType.FLOE)
                .content("test")
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();  // 더미 레코드 객체

        when(recordService.findRecordById(1L)).thenReturn(record);

        mockMvc.perform(get(BASE_PATH + "/{recordId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.DETAIL_RECORD_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.DETAIL_RECORD_GET_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("기록 조회 테스트 (페이징)")
    void getRecords() throws Exception {
        Page<GetRecordResponse> responsePage = Page.empty(); // Mock an empty page for now
        when(recordService.findRecords(any())).thenReturn(responsePage);

        mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "updatedAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RECORD_PAGING_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.RECORD_PAGING_GET_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("기록 검색 테스트(페이징)")
    void searchRecordEmpty() throws Exception {
        Page<GetRecordResponse> responsePage = Page.empty(); // Mock an empty page for now
        when(recordService.searchRecords(any(), any())).thenReturn(responsePage);

        mockMvc.perform(get(BASE_PATH + "/search")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "updatedAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RECORD_SEARCH_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.RECORD_SEARCH_SUCCESS.getMessage()));
    }

}