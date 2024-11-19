package project.floe.domain.record.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.floe.domain.record.dto.request.UpdateMediaRequest;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.CreateRecordResponse;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.entity.Media;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.service.RecordService;
import project.floe.global.result.ResultCode;

@WebMvcTest(RecordController.class)
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
    @DisplayName("Record 수정 테스트")
    void updateRecordTest() throws Exception {
        // Given
        Long recordId = 1L;

        List<String> tags = new ArrayList<>(Arrays.asList("Spring", "Django"));

        UpdateMediaRequest updateMediaRequest1 = UpdateMediaRequest.builder()
                .mediaId(1L)
                .mediaUrl("www.example1.com")
                .build();

        UpdateMediaRequest updateMediaRequest2 = UpdateMediaRequest.builder()
                .mediaId(2L)
                .mediaUrl("www.example2.com")
                .build();

        UpdateRecordRequest updateRecordRequest = UpdateRecordRequest.builder()
                .title("수정 후 테스트")
                .content("테스트 입니다")
                .recordType(RecordType.ISSUE)
                .tags(tags)
                .medias(List.of(updateMediaRequest1, updateMediaRequest2))
                .build();

        MockMultipartFile updateDto = new MockMultipartFile(
                "updateDto",
                "updateDto",
                "application/json",
                ("{\"title\":\"수정 후 테스트\",\"content\":\"테스트 입니다\",\"recordType\":\"ISSUE\"," +
                        "\"tags\":[\"Spring\",\"Django\"],\"medias\":[{" +
                        "\"mediaId\":1,\"mediaUrl\":\"www.example1.com\"},{" +
                        "\"mediaId\":2,\"mediaUrl\":\"www.example2.com\"}]}").getBytes()
        );

        MockMultipartFile updateFile1 = new MockMultipartFile(
                "updateFiles",
                "file1.png",
                "image/png",
                "Dummy file content 1".getBytes()
        );

        MockMultipartFile updateFile2 = new MockMultipartFile(
                "updateFiles",
                "file2.png",
                "image/png",
                "Dummy file content 2".getBytes()
        );

        // when
        when(recordService.modifyRecord(anyLong(), any(UpdateRecordRequest.class), anyList())).thenReturn(mock(Record.class));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_PATH + "/{recordId}", recordId)
                        .file(updateDto)
                        .file(updateFile1)
                        .file(updateFile2)
                        .with(request -> {
                            request.setMethod("PUT"); // multipart에서는 기본이 POST이므로 PUT으로 변경
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RECORD_MODIFY_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.RECORD_MODIFY_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("Record 생성 테스트")
    void createRecordTest() throws Exception {
        // given
        Record record = Record.builder()
                .userId(1L)
                .title("recordTitle")
                .content("recordContent")
                .recordType(RecordType.FLOE)
                .build();

        MockMultipartFile file1 = new MockMultipartFile(
                "files",                     // 필드 이름
                "test1.jpg",                 // 원본 파일 이름
                "image/jpeg",                // MIME 타입
                "test image content 1".getBytes() // 파일 내용
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );

        // dto를 JSON 형태로 직렬화하여 MockMultipartFile로 생성
        String dtoJson = "{\"userId\":1,\"title\":\"recordTitle\",\"content\":\"recordContent\",\"recordType\":\"FLOE\"}";
        MockMultipartFile dto = new MockMultipartFile(
                "dto",                // 필드 이름
                "dto.json",           // 파일 이름
                "application/json",   // MIME 타입
                dtoJson.getBytes()    // DTO JSON 내용
        );

        CreateRecordResponse response = CreateRecordResponse.builder().recordId(1L).build();

        // when
        when(recordService.createRecord(record, List.of("JAVA", "SPRING"), List.of(file1, file2))).thenReturn(response.getRecordId());

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(BASE_PATH)
                                .file(file1)
                                .file(file2)
                                .file(dto)  // DTO 파일 추가
                                .contentType(MediaType.MULTIPART_FORM_DATA)  // multipart/form-data 방식으로 요청
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("개별 Record 조회 테스트")
    void getDetailRecordTest() throws Exception {
        // given
        List<Media> mediaList = Arrays.asList(
                Media.builder().mediaUrl("http://example.com/media1.jpg").build(),
                Media.builder().mediaUrl("http://example.com/media2.jpg").build()
        );

        Record result = Record.builder()
                .userId(1L)
                .title("RecordTitle")
                .content("RecordContent")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(mediaList)
                .build();

        when(recordService.findRecordById(anyLong())).thenReturn(result);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_PATH + "/{recordId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Record 전체 조회 테스트")
    void getRecordsTest() throws Exception {
        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "files",                     // 필드 이름
                "test1.jpg",                 // 원본 파일 이름
                "image/jpeg",                // MIME 타입
                "test image content 1".getBytes() // 파일 내용
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );

        Record record1 = Record.builder()
                .userId(1L)
                .title("test 1")
                .content("test")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();
        Long createdRecordId = recordService.createRecord(record1, List.of("Java", "Spring"), List.of(file1, file2));
        assertNotNull(createdRecordId, "Record ID should not be null after creation");

        Record record2 = Record.builder()
                .userId(1L)
                .title("test 2")
                .content("test")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();
        Long createdRecordId2 = recordService.createRecord(record2, List.of("Java", "Spring"), List.of(file1, file2));
        assertNotNull(createdRecordId2, "Record ID should not be null after creation");

        List<GetRecordResponse> result = List.of(
                GetRecordResponse.from(record1),
                GetRecordResponse.from(record2)
        );

        when(recordService.findRecords(any(Pageable.class))).thenReturn(result);

        List<GetRecordResponse> actualRecords = recordService.findRecords(Pageable.unpaged());
        assertThat(actualRecords).hasSize(2);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("Record 삭제 테스트")
    void deleteRecordTest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_PATH + "/{recordId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}