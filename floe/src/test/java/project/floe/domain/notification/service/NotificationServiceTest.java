package project.floe.domain.notification.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.dto.response.ConnectSuccessResponseDto;
import project.floe.domain.notification.dto.response.GetNotificationListResponseDto;
import project.floe.domain.notification.entity.Notification;
import project.floe.domain.notification.entity.NotificationType;
import project.floe.domain.notification.redis.service.RedisMessageService;
import project.floe.domain.notification.repository.NotificationRepository;
import project.floe.domain.user.entity.User;
import project.floe.global.auth.jwt.service.JwtService;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private RedisMessageService redisMessageService;

    private User user() {
        return User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("nickname")
                .profileImage(null)
                .build();
    }

    private Notification notification() {
        return Notification.builder()
                .id(1L)
                .notificationType(NotificationType.NEW_COMMENT)
                .receiverEmail("test@test.com")
                .sender(user())
                .relatedUrl(null)
                .createdAt(null)
                .isRead(false)
                .build();
    }

    @Test
    void 알림조회() {
        Notification notification = notification();
        String email = notification.getReceiverEmail();

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(notification);
        List<NotificationDto> expected = NotificationDto.listOf(notificationList);

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(notificationList).when(notificationRepository).findByReceiverEmailOrderByCreatedAtDesc(email);

        GetNotificationListResponseDto result = notificationService.getNotificationList(
                mock(HttpServletRequest.class));

        assertThat(expected.get(0).getId()).isEqualTo(result.getNotificationList().get(0).getId());
    }

    @Test
    void 알림읽음처리() {
        Notification notification = notification();
        String email = notification.getReceiverEmail();

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(Optional.of(notification)).when(notificationRepository).findById(notification.getId());

        notificationService.readNotification(mock(HttpServletRequest.class), notification.getId());

        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void 읽지않은알림전부읽음처리() {
        Notification notification = notification();
        String email = notification.getReceiverEmail();

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(notification);

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(notificationList).when(notificationRepository).findByReceiverEmailAndIsRead(email, false);

        notificationService.readAllUnreadNotification(mock(HttpServletRequest.class));

        verify(notificationRepository, times(1)).saveAll(notificationList);
    }

    @Test
    void 알림삭제() {
        Notification notification = notification();
        String email = notification.getReceiverEmail();

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(Optional.of(notification)).when(notificationRepository).findById(notification.getId());

        notificationService.deleteNotification(mock(HttpServletRequest.class), notification.getId());

        verify(notificationRepository, times(1)).delete(notification);
    }

    @Test
    void 읽은알림전부삭제() {
        Notification notification = notification();
        String email = notification.getReceiverEmail();

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(notification);

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(notificationList).when(notificationRepository).findByReceiverEmailAndIsRead(email, true);

        notificationService.deleteAllReadNotification(mock(HttpServletRequest.class));

        verify(notificationRepository, times(1)).deleteAll(notificationList);
    }

    @Test
    void 읽지않은알림수조회() {
        String email = "test@test.com";

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));

        notificationService.getUnreadNotificationCount(mock(HttpServletRequest.class));

        verify(notificationRepository, times(1)).countByReceiverEmailAndIsRead(email, false);
    }

    @Test
    void sse_구독() {
        String email = "test@test.com";
        SseEmitter mockSseEmitter = mock(SseEmitter.class);

        doReturn(Optional.of(email)).when(jwtService).extractEmail(any(HttpServletRequest.class));
        doReturn(mockSseEmitter).when(sseEmitterService).createEmitter(email);

        notificationService.subscribe(mock(HttpServletRequest.class));

        verify(sseEmitterService, times(1)).createEmitter(email);
        verify(sseEmitterService, times(1)).send(any(ConnectSuccessResponseDto.class), eq(email), eq(mockSseEmitter));
        verify(redisMessageService, times(1)).subscribe(email);
        verify(mockSseEmitter, times(1)).onTimeout(any());
        verify(mockSseEmitter, times(1)).onError(any());
        verify(mockSseEmitter, times(1)).onCompletion(any());
    }

    @Test
    void 알림전송() {
        Notification notification = notification();
        User sender = user();
        NotificationDto notificationDto = new NotificationDto(notification);

        doReturn(notification).when(notificationRepository).save(any(Notification.class));

        notificationService.sendNotification(notificationDto, sender);

        verify(redisMessageService, times(1)).publish(anyString(), any(NotificationDto.class));
    }

}
