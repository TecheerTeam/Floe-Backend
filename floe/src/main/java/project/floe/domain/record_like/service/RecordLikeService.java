package project.floe.domain.record_like.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.dto.response.GetRecordLikeListResponseDto;
import project.floe.domain.record_like.entity.RecordLike;
import project.floe.domain.record_like.repository.RecordLikeRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class RecordLikeService {
    private final RecordLikeRepository recordLikeRepository;
    private final RecordService recordService;
    private final UserRepository userRepository;

    @Transactional
    public GetRecordLikeCountResponseDto getRecordLikeCount(Long recordId) {
        recordService.findRecordById(recordId);
        long count = countByRecordId(recordId);
        return new GetRecordLikeCountResponseDto(count);
    }

    public long countByRecordId(Long recordId) {
        return recordLikeRepository.countByRecordId(recordId);
    }

    @Transactional
    public void addRecordLike(Long userId, Long recordId) {
        Record record = recordService.findRecordById(recordId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_ERROR));

        if (recordLikeRepository.findByUserIdAndRecordId(userId, recordId).isPresent()) {
            throw new BusinessException(ErrorCode.RECORD_ALREADY_LIKED_ERROR);
        }

        recordLikeRepository.save(new RecordLike(user, record));
    }

    @Transactional
    public void deleteRecordLike(Long userId, Long recordId) {
        recordService.findRecordById(recordId);

        userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_ERROR));

        Optional<RecordLike> optionalRecordLike = recordLikeRepository.findByUserIdAndRecordId(userId, recordId);
        if (optionalRecordLike.isEmpty()) {
            throw new BusinessException(ErrorCode.RECORD_LIKE_NOT_FOUNT_ERROR);
        }

        recordLikeRepository.delete(optionalRecordLike.get());
    }

    @Transactional
    public GetRecordLikeListResponseDto getRecordLikeList(Long recordId) {
        recordService.findRecordById(recordId);

        List<RecordLike> recordLikeList = findByRecordId(recordId);
        List<User> userList = recordLikeList.stream()
                .map(recordLike -> userRepository.findById(recordLike.getUser().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return new GetRecordLikeListResponseDto(userList);
    }

    public List<RecordLike> findByRecordId(Long recordId) {
        return recordLikeRepository.findByRecordId(recordId);
    }
}
