package project.floe.domain.record.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.repository.RecordJpaRepository;

@SpringBootTest
@Slf4j
class MediaServiceTest {

    private Record newRecord;
    private MultipartFile file1;
    private MultipartFile file2;
    
    @Autowired
    private MediaService mediaService;
    @Autowired
    private RecordJpaRepository recordJpaRepository;

    @BeforeEach
    void init() {
        newRecord = Record.builder()
                .userId(1L)
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .build();
        recordJpaRepository.save(newRecord);

        // MockMultipartFile 생성
        file1 = new MockMultipartFile(
                "files",                     // 필드 이름
                "test1.jpg",                 // 원본 파일 이름
                "image/jpeg",                // MIME 타입
                "test image content 1".getBytes() // 파일 내용
        );

        file2 = new MockMultipartFile(
                "files",
                "test2.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );
    }

    @Test
    @Transactional
    void uploadFilesTest() {
        // when
        mediaService.uploadFiles(newRecord, List.of(file1, file2));
    }

    @Test
    void uploadToS3Test() {
        // when
        String uploadedUrl = mediaService.uploadToS3(file1);
        // then
        log.info("uploaded_url = {}", uploadedUrl);
    }

    @Test
    void deleteFileTest() {
        // given
        String uploadedUrl = mediaService.uploadToS3(file1);

        //when
        mediaService.deleteFile(uploadedUrl);
    }
}