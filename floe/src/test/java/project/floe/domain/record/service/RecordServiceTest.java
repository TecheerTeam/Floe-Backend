package project.floe.domain.record.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
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
class RecordServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordJpaRepository repository;

    private Record newRecord;
    private MultipartFile file1;
    private MultipartFile file2;

    @BeforeEach
    void init() {
        newRecord = Record.builder()
                .userId(1L)
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .build();
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
    void saveTest(){
        Long savedId = recordService.save(newRecord,List.of(file1,file2));

        // then
        Record savedRecord = repository.findById(savedId).orElseThrow(() -> new RuntimeException("Record not found"));
        assertThat(savedId).isEqualTo(savedRecord.getId());
        assertThat(savedRecord.getTitle()).isEqualTo(newRecord.getTitle());
        assertThat(savedRecord.getContent()).isEqualTo(newRecord.getContent());
        assertThat(savedRecord.getUserId()).isEqualTo(newRecord.getUserId());
        assertThat(savedRecord.getRecordType()).isEqualTo(newRecord.getRecordType());
    }
}