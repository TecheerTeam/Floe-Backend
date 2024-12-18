package project.floe.domain.notification.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import project.floe.domain.notification.dto.NotificationDto;

@Getter
@AllArgsConstructor
public class GetNotificationListResponseDto {
    private List<NotificationDto> notificationList;
}
