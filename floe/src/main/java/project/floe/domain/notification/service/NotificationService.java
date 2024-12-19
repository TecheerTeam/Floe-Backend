package project.floe.domain.notification.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.dto.response.ConnectSuccessResponseDto;
import project.floe.domain.notification.dto.response.GetNotificationListResponseDto;
import project.floe.domain.notification.dto.response.GetUnreadCountResponseDto;
import project.floe.domain.notification.entity.Notification;
import project.floe.domain.notification.redis.service.RedisMessageService;
import project.floe.domain.notification.repository.NotificationRepository;
import project.floe.domain.user.entity.User;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.BusinessException;
import project.floe.global.error.exception.UserServiceException;
import project.floe.global.result.ResultCode;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final RedisMessageService redisMessageService;
    private final JwtService jwtService;

    public SseEmitter subscribe(HttpServletRequest request){
        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        SseEmitter sseEmitter = sseEmitterService.createEmitter(email);
        sseEmitterService.send(new ConnectSuccessResponseDto(ResultCode.NOTIFICATION_CONNECT_SUCCESS),email,sseEmitter);

        redisMessageService.subscribe(email);

        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError((e) -> sseEmitter.complete());
        sseEmitter.onCompletion(() -> {
            sseEmitterService.deleteEmitter(email);
            redisMessageService.removeSubscribe(email);
        });
        return sseEmitter;
    }

    @Transactional
    public void sendNotification(NotificationDto notificationDto, User sender){
        Notification notification = notificationRepository.save(new Notification(notificationDto,sender));
        redisMessageService.publish(notification.getReceiverEmail(),new NotificationDto(notification));
    }

    public GetNotificationListResponseDto getNotificationList(HttpServletRequest request){
        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        List<Notification> notificationList = notificationRepository.findByReceiverEmailOrderByCreatedAtDesc(email);
        List<NotificationDto> notificationDtoList = NotificationDto.listOf(notificationList);

        return new GetNotificationListResponseDto(notificationDtoList);
    }

    @Transactional
    public void readNotification(HttpServletRequest request, Long notificationId){
        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        Notification notification = findById(notificationId);

        if(!notification.getReceiverEmail().equals(email)) throw new BusinessException(ErrorCode.NOTIFICATION_NOT_OWNED_BY_USER);

        if(notification.getIsRead()) throw new BusinessException(ErrorCode.NOTIFICATION_ALREADY_READ_ERROR);

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void readAllUnreadNotification(HttpServletRequest request){
        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        List<Notification> notificationList = notificationRepository.findByReceiverEmailAndIsRead(email, false);

        notificationList.forEach(notification -> notification.setIsRead(true));

        notificationRepository.saveAll(notificationList);
    }

    @Transactional
    public void deleteNotification(HttpServletRequest request, Long notificationId){
        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        Notification notification = findById(notificationId);

        if(!notification.getReceiverEmail().equals(email)) throw new BusinessException(ErrorCode.NOTIFICATION_NOT_OWNED_BY_USER);

        notificationRepository.delete(notification);
    }

    @Transactional
    public void deleteAllReadNotification(HttpServletRequest request){
        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        List<Notification> notificationList = notificationRepository.findByReceiverEmailAndIsRead(email, true);

        notificationRepository.deleteAll(notificationList);
    }

    public GetUnreadCountResponseDto getUnreadNotificationCount(HttpServletRequest request){
        String email = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        Long count = notificationRepository.countByReceiverEmailAndIsRead(email, false);

        return new GetUnreadCountResponseDto(count);
    }

    public Notification findById(Long notificationId){
        return notificationRepository.findById(notificationId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND_ERROR));
    }

}
