package project.floe.domain.record_save.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.record_save.dto.response.GetSaveRecordsResponseDto;
import project.floe.domain.record_save.entity.RecordSave;
import project.floe.domain.record_save.repository.RecordSaveRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RecordSaveServiceTest {

    @InjectMocks
    private RecordSaveService recordSaveService;

    @Mock
    private RecordSaveRepository recordSaveRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecordService recordService;
    @Mock
    private JwtService jwtService;

    @Test
    public void 기록저장() {
        String email = "email@email.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        Record record = Record.builder()
                .id(1L)
                .build();

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(record).when(recordService).findRecordById(record.getId());
        doReturn(Optional.of(user)).when(userRepository).findByEmail(email);
        doReturn(Optional.empty()).when(recordSaveRepository).findByUserIdAndRecordId(user.getId(), record.getId());

        recordSaveService.addRecordSave(record.getId(), mock(HttpServletRequest.class));

        verify(recordSaveRepository, times(1)).save(any(RecordSave.class));
    }

    @Test
    void 기록저장삭제() {
        String email = "email@email.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        Record record = Record.builder()
                .id(1L)
                .build();
        RecordSave recordSave = new RecordSave(user, record);

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(record).when(recordService).findRecordById(record.getId());
        doReturn(Optional.of(user)).when(userRepository).findByEmail(email);
        doReturn(Optional.of(recordSave)).when(recordSaveRepository)
                .findByUserIdAndRecordId(user.getId(), record.getId());

        recordSaveService.deleteRecordSave(record.getId(), mock(HttpServletRequest.class));

        verify(recordSaveRepository, times(1)).delete(recordSave);
    }

    @Test
    void 기록저장개수조회() {
        Record record = Record.builder()
                .id(1L)
                .build();

        doReturn(record).when(recordService).findRecordById(record.getId());
        doReturn(0L).when(recordSaveRepository).countByRecordId(record.getId());

        recordSaveService.getSaveCountByRecordId(record.getId());

        verify(recordSaveRepository, times(1)).countByRecordId(record.getId());
    }

    @Test
    void 저장한기록목록조회() {
        String email = "email@email.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> recordPage = new PageImpl<>(List.of());
        List<Long> recordIds = new ArrayList<>();
        List<GetSaveRecordsResponseDto> responseDtoPage = new ArrayList<>();

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(Optional.of(user)).when(userRepository).findByEmail(email);
        doReturn(recordPage).when(recordSaveRepository).findRecordsByUserId(user.getId(), pageable);
        doReturn(responseDtoPage).when(recordSaveRepository).findMediasByRecordIds(recordIds);

        recordSaveService.getSaveRecordList(pageable, mock(HttpServletRequest.class));

        verify(recordSaveRepository, times(1)).findRecordsByUserId(user.getId(), pageable);
        verify(recordSaveRepository, times(1)).findMediasByRecordIds(recordIds);
    }
}
