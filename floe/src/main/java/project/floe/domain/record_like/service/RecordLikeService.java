package project.floe.domain.record_like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.repository.RecordLikeRepository;

@Service
@RequiredArgsConstructor
public class RecordLikeService {
    private final RecordLikeRepository recordLikeRepository;
    private final RecordService recordService;

    @Transactional
    public GetRecordLikeCountResponseDto getRecordLikeCount(Long recordId) {
        recordService.findRecordById(recordId);
        long count = recordLikeRepository.countByRecordId(recordId);
        return new GetRecordLikeCountResponseDto(count);
    }

}
