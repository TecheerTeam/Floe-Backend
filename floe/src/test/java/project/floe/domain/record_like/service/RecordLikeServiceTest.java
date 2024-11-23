package project.floe.domain.record_like.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.floe.domain.record.service.RecordService;
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
    @Mock
    private RecordService recordService;

    @Test
    public void 좋아요수조회실패_존재하지않는기록(){
        Long noExistId = 0L;
        doThrow(new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR)).
                when(recordService).findRecordById(noExistId);

        EmptyResultException response = assertThrows(EmptyResultException.class,
                () -> recordLikeService.getRecordLikeCount(noExistId));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.RECORD_NOT_FOUND_ERROR);
    }

    @Test
    public void 좋아요수조회성공(){
        Long recordId = 0L;
        long expectedCount =1L;
        doReturn(null).when(recordService).findRecordById(recordId);
        doReturn(expectedCount).when(recordLikeRepository).countByRecordId(recordId);

        GetRecordLikeCountResponseDto dto = recordLikeService.getRecordLikeCount(recordId);

        assertThat(dto.getCount()).isEqualTo(expectedCount);
    }
}
