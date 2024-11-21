package project.floe.domain.record.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.entity.Media;
import project.floe.domain.record.entity.Record;
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
        mediaService.deleteFiles(findRecordById(recordId).getMedias());
        try {
            recordRepository.deleteById(recordId);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultException(ErrorCode.RECORD_DELETED_OR_NOT_EXIST);
        }
    }

    public Record findRecordById(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR));
    }

    public Page<GetRecordResponse> findRecords(Pageable pageable) {
        Page<Record> records = recordRepository.findAll(pageable);
        return GetRecordResponse.listOf(records);
    }

    @Transactional
    public Record modifyRecord(Long recordId, UpdateRecordRequest dto, List<MultipartFile> files){
        Record findRecord = findRecordById(recordId);
        List<Media> updatedMedias = mediaService.updateMedias(findRecord, dto.getMedias() ,files);
        Tags updatedTags = tagService.createTags(dto.getTags());
        findRecord.updateRecord(dto.getTitle(), dto.getContent(), dto.getRecordType(), updatedTags, updatedMedias);
        return findRecord;
    }

}
