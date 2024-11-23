package project.floe.domain.record_like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.repository.RecordLikeRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.EmptyResultException;

@Service
@RequiredArgsConstructor
public class RecordLikeService {
    private final RecordLikeRepository recordLikeRepository;

    public GetRecordLikeCountResponseDto getRecordLikeCount(Long recordId) {
        boolean isExisted = recordLikeRepository.existsByRecordId(recordId);
        if (!isExisted) {
            throw new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR);
        }

        long count = recordLikeRepository.countByRecordId(recordId);
        return new GetRecordLikeCountResponseDto(count);
    }
}
