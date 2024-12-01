package project.floe.domain.record_save.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.record_save.entity.RecordSave;
import project.floe.domain.record_save.repository.RecordSaveRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;

@ExtendWith(MockitoExtension.class)
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
    void 기록삭제() {
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
        doReturn(Optional.of(recordSave)).when(recordSaveRepository).findByUserIdAndRecordId(user.getId(), record.getId());

        recordSaveService.deleteRecordSave(record.getId(), mock(HttpServletRequest.class));

        verify(recordSaveRepository, times(1)).delete(any(RecordSave.class));
    }
}
