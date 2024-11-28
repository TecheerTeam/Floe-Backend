package project.floe.domain.record_like.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.entity.RecordLike;
import project.floe.domain.record_like.repository.RecordLikeRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.BusinessException;
import project.floe.global.error.exception.EmptyResultException;

@ExtendWith(MockitoExtension.class)
public class RecordLikeServiceTest {

    @InjectMocks
    private RecordLikeService recordLikeService;
    @Mock
    private RecordLikeRepository recordLikeRepository;
    @Mock
    private RecordService recordService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 좋아요수조회_존재하지않는기록() {
        Long noExistId = 1L;
        doThrow(new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR)).
                when(recordService).findRecordById(noExistId);

        EmptyResultException response = assertThrows(EmptyResultException.class,
                () -> recordLikeService.getRecordLikeCount(noExistId));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.RECORD_NOT_FOUND_ERROR);
    }

    @Test
    public void 좋아요수조회() {
        Long recordId = 1L;
        long expectedCount = 1L;
        doReturn(null).when(recordService).findRecordById(recordId);
        doReturn(expectedCount).when(recordLikeRepository).countByRecordId(recordId);

        GetRecordLikeCountResponseDto dto = recordLikeService.getRecordLikeCount(recordId);

        assertThat(dto.getCount()).isEqualTo(expectedCount);
    }

    @Test
    public void 좋아요추가() {
        User user = user();
        Record record = record();
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(record).when(recordService).findRecordById(record.getId());
        doReturn(Optional.empty()).when(recordLikeRepository).findByUserIdAndRecordId(user.getId(), record.getId());

        recordLikeService.addRecordLike(user.getId(), record.getId());

        verify(recordLikeRepository, times(1)).save(any(RecordLike.class));
    }

    @Test
    public void 좋아요추가_중복() {
        User user = user();
        Record record = record();
        RecordLike recordLike = new RecordLike(user, record);
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(record).when(recordService).findRecordById(record.getId());
        doReturn(Optional.of(recordLike)).when(recordLikeRepository).findByUserIdAndRecordId(user.getId(), record.getId());

        BusinessException response = assertThrows(BusinessException.class,
                () -> recordLikeService.addRecordLike(user.getId(), record.getId()));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.RECORD_ALREADY_LIKED_ERROR);
    }

    @Test
    public void 좋아요삭제() {
        User user = user();
        Record record = record();
        RecordLike recordLike = new RecordLike(user, record);
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(record).when(recordService).findRecordById(record.getId());
        doReturn(Optional.of(recordLike)).when(recordLikeRepository).findByUserIdAndRecordId(user.getId(), record.getId());

        recordLikeService.deleteRecordLike(user.getId(), record.getId());

        verify(recordLikeRepository, times(1)).delete(recordLike);
    }

    @Test
    public void 좋아요삭제_존재하지않는좋아요() {
        User user = user();
        Record record = record();
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(record).when(recordService).findRecordById(record.getId());
        doReturn(Optional.empty()).when(recordLikeRepository).findByUserIdAndRecordId(user.getId(), record.getId());

        BusinessException response = assertThrows(BusinessException.class,
                () -> recordLikeService.deleteRecordLike(user.getId(), record.getId()));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.RECORD_LIKE_NOT_FOUNT_ERROR);
    }

    @Test
    public void 좋아요목록조회(){
        User user = user();
        Record record = record();
        List<RecordLike> list = new ArrayList<>();
        list.add(new RecordLike(user,record));
        doReturn(list).when(recordLikeRepository).findByRecordId(record.getId());

        List<RecordLike> recordLikeList = recordLikeService.findByRecordId(record.getId());

        assertThat(recordLikeList).isEqualTo(list);
    }

    @Test
    public void 좋아요한유저목록조회(){
        User user = user();
        Record record = record();
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());

        List<RecordLike> list = new ArrayList<>();
        list.add(new RecordLike(user,record));
        doReturn(list).when(recordLikeRepository).findByRecordId(record.getId());

        recordLikeService.getRecordLikeList(record.getId());

        verify(userRepository,times(list.size())).findById(null);
    }

    public User user() {
        return User.builder().email("test@example.com").password("1234").nickname("test").build();

    }

    public Record record() {
        return Record.builder()
                .id(null)
                .user(user())
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .build();
    }
}
