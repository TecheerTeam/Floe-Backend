package project.floe.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.user.entity.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "receiver_email", nullable = false)
    private String receiverEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "related_url")
    private String relatedUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    public Notification(NotificationDto notificationDto, User sender) {
        this.notificationType = notificationDto.getNotificationType();
        this.receiverEmail = notificationDto.getReceiverEmail();
        this.relatedUrl = notificationDto.getRelatedUrl();
        this.sender = sender;
        this.isRead = false;
    }
}
