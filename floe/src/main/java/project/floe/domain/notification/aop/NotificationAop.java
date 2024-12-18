package project.floe.domain.notification.aop;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import project.floe.domain.comment.dto.request.CreateCommentRequest;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.entity.NotificationType;
import project.floe.domain.notification.service.NotificationService;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;

@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAop {

    private final NotificationService notificationService;
    private final RecordService recordService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private static final String RECORD_URL = "/api/v1/records/";


    @Pointcut(value = "execution(* project.floe.domain.comment.service.CommentService.createComment(..)) && args(request,httpServletRequest)", argNames = "request,httpServletRequest")
    private void commentNotification(CreateCommentRequest request, HttpServletRequest httpServletRequest) {
    }

    @AfterReturning(value = "commentNotification(request,httpServletRequest)", argNames = "request,httpServletRequest")
    public void sendNotification(CreateCommentRequest request, HttpServletRequest httpServletRequest) {
        Long recordId = request.getRecordId();
        Record record = recordService.findRecordById(recordId);
        String receiverEmail = record.getUser().getEmail();

        Optional<String> extractedEmail = jwtService.extractEmail(httpServletRequest);
        String senderEmail = extractedEmail.get();

        if(receiverEmail.equals(senderEmail)) return;

        Optional<User> user = userRepository.findByEmail(senderEmail);
        User sender = user.get();

        NotificationType notificationType = NotificationType.NEW_COMMENT;
        if (request.getParentId() != null) {
            notificationType = NotificationType.NEW_REPLY;
        }

        String relatedUrl = RECORD_URL + request.getRecordId().toString();

        NotificationDto notificationDto = NotificationDto.from(notificationType, receiverEmail, sender, relatedUrl);
        notificationService.sendNotification(notificationDto,sender);
    }
}
