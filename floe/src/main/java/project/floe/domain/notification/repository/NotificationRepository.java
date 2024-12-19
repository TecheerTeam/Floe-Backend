package project.floe.domain.notification.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.floe.domain.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverEmailOrderByCreatedAtDesc(String receiverEmail);

    List<Notification> findByReceiverEmailAndIsRead(String receiverEmail, Boolean isRead);

    Long countByReceiverEmailAndIsRead(String receiverEmail, Boolean isRead);
}
