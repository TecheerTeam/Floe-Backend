package project.floe.domain.record.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTag;
import project.floe.domain.record.entity.Tags;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.EmptyResultException;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final MediaService mediaService;
    private final RecordJpaRepository recordRepository;
    private final TagService tagService;

    @Transactional
    public Long createRecord(Record record, List<String> tagNames, List<MultipartFile> files) {
        if (tagNames != null){
            Tags findTags = tagService.createTags(tagNames);
            record.addTag(findTags);
        }
        Record savedRecord = recordRepository.save(record);
        mediaService.uploadFiles(savedRecord, files);
        return savedRecord.getId();
    }

    @Transactional
    public void deleteRecord(Long recordId) {
        recordRepository.deleteById(recordId);
    }

    public Record findRecordById(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR));
    }
}
