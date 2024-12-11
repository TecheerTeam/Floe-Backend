package project.floe.domain.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.request.SearchRecordRequest;
import project.floe.domain.record.dto.request.UpdateMediaRequest;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.dto.response.UserRecordsResponse;
import project.floe.domain.record.entity.Media;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.entity.Tag;
import project.floe.domain.record.entity.Tags;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.domain.record.repository.RecordTagJpaRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.EmptyKeywordException;
import project.floe.global.error.exception.EmptyResultException;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @InjectMocks
    private RecordService recordService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TagService tagService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private RecordJpaRepository recordRepository;

    @Mock
    private RecordTagJpaRepository recordTagRepository;

    @Test
    @DisplayName("레코드 생성 테스트")
    void 레코드_생성() {
        // given
        String email = "test@naver.com";
        User mockUser = User.builder()
                .email(email)
                .build();

        List<String> tagNames = new ArrayList<>(Arrays.asList("Spring", "Django"));

        Tags mockTags = new Tags(Arrays.asList(
                Tag.builder().id(1L).tagName("Spring").build(),
                Tag.builder().id(2L).tagName("Django").build()
        ));

        CreateRecordRequest dto = CreateRecordRequest.builder()
                .title("test")
                .content("test content")
                .recordType(RecordType.FLOE)
                .tagNames(tagNames)
                .build();

        List<MultipartFile> mockFiles = Arrays.asList(
                new MockMultipartFile("file1", "test1.png", "image/png", new byte[]{}),
                new MockMultipartFile("file2", "test2.png", "image/png", new byte[]{})
        );

        Record savedRecord = Record.builder()
                .id(1L)
                .title(dto.getTitle())
                .content(dto.getContent())
                .recordType(dto.getRecordType())
                .build();

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(tagService.createTags(anyList())).thenReturn(mockTags);
        when(recordRepository.save(any(Record.class))).thenReturn(savedRecord);

        // When
        Long recordId = recordService.createRecord(mock(HttpServletRequest.class), dto, mockFiles);

        // Then
        assertThat(recordId).isEqualTo(savedRecord.getId());
        verify(recordRepository).save(any(Record.class));
        verify(mediaService).uploadFiles(any(Record.class), anyList());
    }

    @Test
    @DisplayName("레코드 삭제 테스트")
    void 레코드_삭제() {
        // Given
        Long recordId = 1L;
        Record mockRecord = Record.builder()
                .id(recordId)
                .medias(List.of(Media.builder().id(1L).build()))
                .build();

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
        doNothing().when(mediaService).deleteFiles(anyList());

        // When
        recordService.deleteRecord(recordId);

        // Then
        verify(mediaService).deleteFiles(mockRecord.getMedias());
        verify(recordRepository).deleteById(recordId);
    }

    @Test
    @DisplayName("레코드 삭제 - 존재하지 않는 경우")
    void 레코드_삭제_예외() {
        // Given
        Long recordId = 1L;
        when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

        // When & Then
        EmptyResultException exception = assertThrows(EmptyResultException.class,
                () -> recordService.deleteRecord(recordId));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECORD_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("레코드 조회 테스트")
    void 레코드_조회() {
        // Given
        Long recordId = 1L;
        Record mockRecord = Record.builder()
                .id(recordId)
                .build();

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));

        // When
        Record foundRecord = recordService.findRecordById(recordId);

        // Then
        assertThat(foundRecord).isEqualTo(mockRecord);
        verify(recordRepository).findById(recordId);
    }

    @Test
    @DisplayName("레코드 조회 - 존재하지 않는 경우")
    void 레코드_조회_예외() {
        // Given
        Long recordId = 1L;
        when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

        // When & Then
        EmptyResultException exception = assertThrows(EmptyResultException.class,
                () -> recordService.findRecordById(recordId));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECORD_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("레코드 목록 조회 테스트")
    void 레코드_목록_조회() {
        // given
        String email = "test@naver.com";
        User mockUser = User.builder()
                .email(email)
                .build();

        Record mockRecord = Record.builder()
                .medias(List.of(Media.builder().id(1L).build()))
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> recordsPage = new PageImpl<>(List.of());
        when(recordRepository.findAll(pageable)).thenReturn(recordsPage);

        // when
        Page<GetRecordResponse> responsePage = recordService.findRecords(pageable);

        // then
        assertThat(responsePage.getTotalElements()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("레코드 수정 테스트")
    void 레코드_수정() {
        // given
        Long recordId = 1L;
        Record existingRecord = Record.builder()
                .id(recordId)
                .title("기존 제목")
                .content("기존 내용")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())  // RecordTags 초기화
                .medias(new ArrayList<>())
                .build();

        UpdateMediaRequest mediaRequest = UpdateMediaRequest.builder().build();
        UpdateRecordRequest dto = UpdateRecordRequest.builder()
                .title("새 제목")
                .content("새 내용")
                .recordType(RecordType.FLOE)
                .tags(List.of("Spring", "JPA"))
                .medias(List.of(mediaRequest))
                .build();

        List<MultipartFile> files = List.of(
                new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[1])
        );

        Tags updatedTags = new Tags(List.of(
                Tag.builder().id(1L).tagName("Spring").build(),
                Tag.builder().id(2L).tagName("JPA").build()
        ));

        List<Media> updatedMedias = List.of(new Media());

        // Mocking 외부 서비스 호출
        when(recordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));
        when(mediaService.updateMedias(any(Record.class), anyList(), anyList())).thenReturn(updatedMedias);
        when(tagService.createTags(anyList())).thenReturn(updatedTags);

        // when
        Record updatedRecord = recordService.modifyRecord(recordId, dto, files);

        // then
        assertThat(updatedRecord.getTitle()).isEqualTo("새 제목");
        assertThat(updatedRecord.getContent()).isEqualTo("새 내용");
        assertThat(updatedRecord.getRecordType()).isEqualTo(RecordType.FLOE);
        assertThat(updatedRecord.getTagNames()).containsExactly("Spring", "JPA");

        verify(recordRepository).findById(recordId);
        verify(mediaService).updateMedias(any(Record.class), anyList(), anyList());
        verify(tagService).createTags(anyList());
    }

    @Test
    @DisplayName("레코드 검색 테스트 - 태그 + 제목 검색")
    void 레코드_검색_태그_제목() {

        String email = "test@naver.com";
        User mockUser = User.builder()
                .email(email)
                .build();

        Tags updatedTags = new Tags(List.of(
                Tag.builder().id(1L).tagName("Spring").build(),
                Tag.builder().id(2L).tagName("JPA").build()
        ));

        Record record = Record.builder()
                .medias(List.of(Media.builder().id(1L).build()))
                .user(mockUser)
                .title("test")
                .content("test content")
                .recordTags(new RecordTags())
                .recordType(RecordType.FLOE)
                .build();

        // Tag를 Record에 추가
        record.addTag(new Tags(updatedTags.getTags()));

        when(recordTagRepository.findRecordsByTagsAndTitleAndRecordType(
                updatedTags.getTagNames(), record.getTitle(), record.getRecordType(), PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(record)));
        recordRepository.save(record);

        // 검색 요청 객체 생성
        SearchRecordRequest searchRequest = SearchRecordRequest.builder()
                .title("test")
                .recordType(RecordType.FLOE)
                .tagNames(List.of("Spring", "JPA"))
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        // When: 검색 수행
        Page<GetRecordResponse> result = recordService.searchRecords(pageable, searchRequest);

        // Then: 결과 검증
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("test");
        assertThat(result.getContent().get(0).getTagNames()).containsExactlyInAnyOrder("Spring", "JPA");

    }

    @Test
    @DisplayName("레코드 검색 테스트 - 태그만 검색")
    void 레코드_검색_태그() {
        // given
        SearchRecordRequest dto = SearchRecordRequest.builder()
                .title("")
                .recordType(RecordType.FLOE)
                .tagNames(List.of("Spring", "JPA"))
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> recordsPage = new PageImpl<>(List.of());
        when(recordTagRepository.findRecordsByTagsAndTitleAndRecordType(dto.getTagNames(), dto.getTitle(),
                dto.getRecordType(), pageable))
                .thenReturn(recordsPage);

        Page<GetRecordResponse> responsePage = recordService.searchRecords(pageable, dto);

        assertThat(responsePage.getTotalElements()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("레코드 검색 테스트 - 제목만 검색")
    void 레코드_검색_제목() {

        SearchRecordRequest dto = SearchRecordRequest.builder()
                .title("test")
                .recordType(RecordType.FLOE)
                .tagNames(new ArrayList<>())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> recordsPage = new PageImpl<>(List.of());
        when(recordRepository.findByTitleContainingAndRecordType(dto.getTitle(), dto.getRecordType(), pageable))
                .thenReturn(recordsPage);

        Page<GetRecordResponse> responsePage = recordService.searchRecords(pageable, dto);

        assertThat(responsePage.getTotalElements()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("레코드 검색 테스트 - 태그 + 제목 없이 검색")
    void 레코드_검색_제목_검색어_없음() {

        SearchRecordRequest dto = SearchRecordRequest.builder()
                .title("")
                .recordType(RecordType.FLOE)
                .tagNames(new ArrayList<>())
                .build();
        EmptyKeywordException exception = assertThrows(EmptyKeywordException.class,
                () -> recordService.searchRecords(PageRequest.of(0, 10), dto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECORD_SEARCH_EMPTY_ERROR);
    }

    @Test
    @DisplayName("사용자가 작성한 게시글들의 id를 가져와 반환해줍니다")
    void 사용자_작성_게시글_id_조회(){
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String email = "test@example.com";
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 2);

        User user = User.builder().email(email).id(userId).build();
        Record record1 = Record.builder()
                .medias(List.of(Media.builder().id(1L).build()))
                .user(user)
                .title("test1")
                .content("test content")
                .recordTags(new RecordTags())
                .recordType(RecordType.FLOE)
                .build();
        Record record2 = Record.builder()
                .medias(List.of(Media.builder().id(1L).build()))
                .user(user)
                .title("test2")
                .content("test content2")
                .recordTags(new RecordTags())
                .recordType(RecordType.FLOE)
                .build();

        Page<Record> mockPage = new PageImpl<>(List.of(record1, record2), pageable, 2);

        // Mocking
        when(jwtService.extractEmail(request)).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(recordRepository.findByUserId(userId, pageable)).thenReturn(mockPage);

        // When
        Page<UserRecordsResponse> result = recordService.getUserRecords(request, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("title")
                .containsExactly("test1", "test2");
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);

    }
}