package project.floe.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.notification.entity.Notification;
import project.floe.domain.notification.entity.NotificationType;
import project.floe.domain.user.entity.User;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    private NotificationType notificationType;

    private String receiverEmail;

    private String senderEmail;

    private String senderNickname;

    private String senderProfileImage;

    private String relatedUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private Boolean isRead;

    public NotificationDto(Notification notification) {
        User sender = notification.getSender();
        this.id = notification.getId();
        this.notificationType = notification.getNotificationType();
        this.receiverEmail = notification.getReceiverEmail();
        this.senderEmail = sender.getEmail();
        this.senderNickname = sender.getNickname();
        this.senderProfileImage = sender.getProfileImage();
        this.relatedUrl = notification.getRelatedUrl();
        this.createdAt = notification.getCreatedAt();
        this.isRead = notification.getIsRead();
    }

    public static NotificationDto from(NotificationType notificationType, String receiverEmail, User sender, String relatedUrl) {
        return NotificationDto.builder()
                .notificationType(notificationType)
                .receiverEmail(receiverEmail)
                .senderEmail(sender.getEmail())
                .senderNickname(sender.getNickname())
                .senderProfileImage(sender.getProfileImage())
                .relatedUrl(relatedUrl)
                .build();
    }

    public static List<NotificationDto> listOf(List<Notification> notificationList) {
        return notificationList.stream()
                .map(NotificationDto::new)
                .toList();
    }
}
