package project.floe.domain.record_like.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
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
        Long noExistId = 1L;
        doThrow(new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR)).
                when(recordService).findRecordById(noExistId);

        EmptyResultException response = assertThrows(EmptyResultException.class,
                () -> recordLikeService.getRecordLikeCount(noExistId));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.RECORD_NOT_FOUND_ERROR);
    }

    @Test
    public void 좋아요수조회성공(){
        Long recordId = 1L;
        long expectedCount =1L;
        doReturn(null).when(recordService).findRecordById(recordId);
        doReturn(expectedCount).when(recordLikeRepository).countByRecordId(recordId);

        GetRecordLikeCountResponseDto dto = recordLikeService.getRecordLikeCount(recordId);

        assertThat(dto.getCount()).isEqualTo(expectedCount);
    }

    @Test
    public void 좋아요추가실패_중복(){
        Long userId = 1L;
        Long recordId = 1L;
        doThrow(new DataIntegrityViolationException(""))
                .when(recordLikeRepository).addLike(userId,recordId);

        EmptyResultException response = assertThrows(EmptyResultException.class,
                () -> recordLikeService.addRecordLike(userId, recordId));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.RECORD_ALREADY_LIKED_ERROR);
    }

    @Test
    public void 좋아요추가성공(){
        Long userId = 1L;
        Long recordId = 1L;
        doNothing().when(recordLikeRepository).addLike(userId,recordId);

        recordLikeService.addRecordLike(userId,recordId);

        verify(recordLikeRepository,times(1)).addLike(userId,recordId);
    }
}
