package project.floe.domain.record_like.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.repository.RecordLikeRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.EmptyResultException;

@ExtendWith(MockitoExtension.class)
public class RecordLikeServiceTest {

    @InjectMocks
    private RecordLikeService recordLikeService;
    @Mock
    private RecordLikeRepository recordLikeRepository;

    @Test
    public void 좋아요수조회실패_존재하지않는기록(){
        Long noExistId = 0L;
        doReturn(false).when(recordLikeRepository).existsByRecordId(noExistId);

        EmptyResultException response = assertThrows(EmptyResultException.class,
                () -> recordLikeService.getRecordLikeCount(noExistId));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.RECORD_NOT_FOUND_ERROR);
    }

    @Test
    public void 좋아요수조회성공(){
        Long recordId = 0L;
        long expectedCount =1L;
        doReturn(true).when(recordLikeRepository).existsByRecordId(recordId);
        doReturn(expectedCount).when(recordLikeRepository).countByRecordId(recordId);

        GetRecordLikeCountResponseDto dto = recordLikeService.getRecordLikeCount(recordId);

        assertThat(dto.getCount()).isEqualTo(expectedCount);
    }
}
