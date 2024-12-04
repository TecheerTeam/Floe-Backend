package project.floe.domain.record_save.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.record_save.dto.response.FindMediasByRecordIdsResponseDto;
import project.floe.domain.record_save.dto.response.GetSaveCountResponseDto;
import project.floe.domain.record_save.dto.response.GetSaveRecordsResponseDto;
import project.floe.domain.record_save.entity.RecordSave;
import project.floe.domain.record_save.repository.RecordSaveRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.BusinessException;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class RecordSaveService {

    private final RecordSaveRepository recordSaveRepository;
    private final RecordService recordService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public void addRecordSave(Long recordId, HttpServletRequest request) {

        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        Record record = recordService.findRecordById(recordId);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        Optional<RecordSave> foundRecordSave = recordSaveRepository.findByUserIdAndRecordId(user.getId(),
                record.getId());
        if (foundRecordSave.isPresent()) {
            throw new BusinessException(ErrorCode.RECORD_ALREADY_SAVED_ERROR);
        }

        RecordSave recordSave = new RecordSave(user, record);
        recordSaveRepository.save(recordSave);
    }

    @Transactional
    public void deleteRecordSave(Long recordId, HttpServletRequest request) {

        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        Record record = recordService.findRecordById(recordId);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        Optional<RecordSave> foundRecordSave = recordSaveRepository.findByUserIdAndRecordId(user.getId(),
                record.getId());
        if (foundRecordSave.isEmpty()) {
            throw new BusinessException(ErrorCode.RECORD_SAVED_NOT_FOUNT_ERROR);
        }

        recordSaveRepository.delete(foundRecordSave.get());
    }

    @Transactional
    public GetSaveCountResponseDto getSaveCountByRecordId(Long recordId) {
        recordService.findRecordById(recordId);
        Long count = recordSaveRepository.countByRecordId(recordId);
        return new GetSaveCountResponseDto(count);
    }

    @Transactional
    public Page<GetSaveRecordsResponseDto> getSaveRecordList(Pageable pageable, HttpServletRequest request) {

        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        Page<Record> foundRecords = recordSaveRepository.findRecordsByUserId(user.getId(), pageable);

        List<Long> foundRecordIds = foundRecords.stream()
                .map(Record::getId)
                .toList();
        List<FindMediasByRecordIdsResponseDto> responseDtoList = recordSaveRepository.findMediasByRecordIds(
                foundRecordIds);

        return GetSaveRecordsResponseDto.listOf(foundRecords, responseDtoList);
    }

}
