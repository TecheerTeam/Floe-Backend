package project.floe.domain.record.controller;

import static com.amazonaws.util.json.Jackson.toJsonString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.floe.global.result.ResultCode.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.response.CreateRecordResponse;
import project.floe.domain.record.dto.response.GetDetailRecordResponse;
import project.floe.domain.record.dto.response.MediaResponse;
import project.floe.domain.record.entity.Media;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.service.RecordService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@WebMvcTest(RecordController.class)
class RecordControllerTest{

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
        when(recordService.save(record, List.of(file1, file2))).thenReturn(response.getRecordId());

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
                .medias(mediaList)
                .build();

        when(recordService.findRecordById(anyLong())).thenReturn(result);

        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_PATH + "/{recordId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
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