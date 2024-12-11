package project.floe.domain.record.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.request.SearchRecordRequest;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.dto.response.UserRecordsResponse;
import project.floe.domain.record.entity.Media;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.Tags;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.domain.record.repository.RecordTagJpaRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.EmptyKeywordException;
import project.floe.global.error.exception.EmptyResultException;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final MediaService mediaService;
    private final RecordJpaRepository recordRepository;
    private final UserRepository userRepository;
    private final RecordTagJpaRepository recordTagRepository;
    private final TagService tagService;
    private final JwtService jwtService;

    @Transactional
    public Long createRecord(HttpServletRequest request, CreateRecordRequest dto, List<MultipartFile> files) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));
        Record record = dto.toEntity(findUser);

        if (dto.getTagNames() != null) {
            Tags findTags = tagService.createTags(dto.getTagNames());
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

    public Page<GetRecordResponse> searchRecords(Pageable pageable, SearchRecordRequest dto) {
        Page<Record> records;

        if (dto.getTagNames() != null && !dto.getTagNames().isEmpty()) {
            records = recordTagRepository.findRecordsByTagsAndTitleAndRecordType(
                    dto.getTagNames(), dto.getTitle(), dto.getRecordType(), pageable);
        } else {
            if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
                throw new EmptyKeywordException(ErrorCode.RECORD_SEARCH_EMPTY_ERROR);
            }
            records = recordRepository.findByTitleContainingAndRecordType(
                    dto.getTitle(), dto.getRecordType(), pageable);
        }

        return GetRecordResponse.listOf(records);
    }

    @Transactional
    public Record modifyRecord(Long recordId, UpdateRecordRequest dto, List<MultipartFile> files) {
        Record findRecord = findRecordById(recordId);
        List<Media> updatedMedias = mediaService.updateMedias(findRecord, dto.getMedias(), files);
        Tags updatedTags = tagService.createTags(dto.getTagNames());
        findRecord.updateRecord(dto.getTitle(), dto.getContent(), dto.getRecordType(), updatedTags, updatedMedias);
        return findRecord;
    }

    public Page<UserRecordsResponse> getUserRecords(HttpServletRequest request, Pageable pageable) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        Page<Record> records = recordRepository.findByUserId(findUser.getId(),pageable);

        return UserRecordsResponse.listOf(records);
    }
}
