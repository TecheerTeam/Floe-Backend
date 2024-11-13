package project.floe.domain.record.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.repository.RecordJpaRepository;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final MediaService mediaService;
    private final RecordJpaRepository repository;

    @Transactional
    public Long save(Record record, List<MultipartFile> files) {
        Record savedRecord = repository.save(record);
        mediaService.uploadFiles(savedRecord, files);
        return savedRecord.getId();
    }
}
