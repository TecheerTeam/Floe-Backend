package project.floe.domain.record.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import project.floe.domain.record.dto.request.UpdateMediaRequest;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.entity.Tag;
import project.floe.domain.record.entity.Tags;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.EmptyResultException;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @InjectMocks
    private RecordService recordService;

    @Mock
    private TagService tagService;

    @Mock
    private MediaService mediaService;

    @Mock
    private RecordJpaRepository recordRepository;

    @Test
    @DisplayName("레코드 생성 테스트")
    void 레코드_생성() {
        // given
        List<String> tags = new ArrayList<>(Arrays.asList("Spring", "Django"));

        Record record = Record.builder()
                .id(1L)
                .userId(1L)
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .build();

        // MockMultipartFile 생성
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

        // when
        when(recordRepository.save(any(Record.class))).thenReturn(record);
        when(tagService.createTags(tags)).thenReturn(new Tags(List.of(
                Tag.builder().tagName("Spring").build(),
                Tag.builder().tagName("Django").build()
        )));
        Long result = recordService.createRecord(record, tags, List.of(file1, file2));

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("레코드 수정 테스트")
    void 레코드_수정() {
        // given
        Record record = Record.builder()
                .id(1L)
                .userId(1L)
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();

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

        Tag tag1 = Tag.builder().tagName("Vue").build();
        Tag tag2 = Tag.builder().tagName("Java").build();

        // when
        when(recordRepository.findById(record.getId())).thenReturn(Optional.of(record));
        when(tagService.createTags(tags)).thenReturn(new Tags(List.of(tag1, tag2)));

        Record result = recordService.modifyRecord(record.getId(), updateRecordRequest, List.of(file1, file2));

        assertThat(result.getTitle()).isEqualTo(record.getTitle());
        assertThat(result.getContent()).isEqualTo(record.getContent());
        assertThat(result.getRecordType()).isEqualTo(record.getRecordType());
        assertThat(result.getMediaIds()).isEqualTo(record.getMediaIds());
        assertThat(result.getRecordTags().getTagNames()).isEqualTo(new ArrayList<>(Arrays.asList("Vue", "Java")));
    }

    @Test
    @DisplayName("레코드 조회 테스트")
    void 레코드_조회() {
        // given
        Record record1 = Record.builder()
                .id(1L)
                .userId(1L)
                .title("테스트1")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();

        Record record2 = Record.builder()
                .id(2L)
                .userId(1L)
                .title("테스트2")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();

        Pageable pageable = PageRequest.of(0, 2);

        List<Record> recordList = new ArrayList<>(Arrays.asList(record1, record2));
        Page<Record> records = new PageImpl<>(recordList);

        List<GetRecordResponse> responses = GetRecordResponse.listOf(records).getContent();

        // when
        when(recordRepository.findAll(pageable)).thenReturn(records);

        List<GetRecordResponse> result = recordService.findRecords(pageable);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("테스트1");
        assertThat(result.get(1).getTitle()).isEqualTo("테스트2");
    }

    @Test
    @DisplayName("레코드 단건 조회 테스트")
    void 레코드_단건_조회() {
        // given
        Record record = Record.builder()
                .id(1L)
                .userId(1L)
                .title("테스트1")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .medias(new ArrayList<>())
                .build();

        // when
        when(recordRepository.findById(record.getId())).thenReturn(Optional.of(record));
        Record result = recordService.findRecordById(record.getId());

        // then
        assertThat(result.getTitle()).isEqualTo(record.getTitle());
        assertThat(result.getContent()).isEqualTo(record.getContent());
    }

    @Test
    @DisplayName("단건 조회 실패 테스트 EmptyResultException 발생")
    void 레코드_단건_조회_실패() {
        // given
        Long recordId = 1L;

        // when
        when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

        // then
        assertThrows(EmptyResultException.class, () -> {
            recordService.findRecordById(recordId);
        });
    }

    @Test
    @DisplayName("레코드 삭제 테스트")
    void 레코드_삭제() throws Exception {
        // given
        Record record = Record.builder()
                .id(1L)
                .userId(1L)
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .build();

        // when
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));  // findRecordById가 Mock되도록 설정
        doNothing().when(mediaService).deleteFiles(any()); // mediaService.deleteFiles도 Mock 설정

        // deleteRecord 호출
        recordService.deleteRecord(1L);

        // then
        verify(recordRepository).deleteById(1L);  // recordRepository의 deleteById가 호출되었는지 확인
        verify(mediaService).deleteFiles(any()); // mediaService의 deleteFiles가 호출되었는지 확인
    }

    @Test
    @DisplayName("레코드 삭제 시 존재하지 않는 기록 예외 테스트")
    void 레코드_삭제_실패() {
        // given
        // Mock recordRepository.findById() 설정
        Record mockRecord = Record.builder().medias(new ArrayList<>()).build();
        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(mockRecord));

        // Mock deleteById() 설정 - 예외가 발생하도      록
        doThrow(EmptyResultDataAccessException.class).when(recordRepository).deleteById(anyLong());

        // then
        // deleteRecord 메서드가 EmptyResultException을 던지는지 확인
        assertThrows(EmptyResultException.class, () -> {
            recordService.deleteRecord(1L);  // 예외 발생 기대
        }, ErrorCode.RECORD_DELETED_OR_NOT_EXIST.getMessage());
    }

}