package project.floe.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import project.floe.domain.notification.dto.response.GetNotificationListResponseDto;
import project.floe.domain.notification.dto.response.GetUnreadCountResponseDto;
import project.floe.domain.notification.service.NotificationService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "NotificationController" , description = "알림 API")
@RequestMapping("/api/v1/notification")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "알림 구독",
            description = "sse 구독으로 알림 발생 시 실시간으로 알림을 받게 된다."
    )
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            HttpServletRequest request
    ) {
        SseEmitter sseEmitter = notificationService.subscribe(request);
        return ResponseEntity.ok(sseEmitter);
    }

    @Operation(
            summary = "알림 리스트 조회",
            description = "사용자의 모든 알림 조회"
    )
    @GetMapping()
    public ResponseEntity<ResultResponse> getNotificationList(
            HttpServletRequest request
    ) {
        GetNotificationListResponseDto responseDto = notificationService.getNotificationList(request);
        ResultResponse response = ResultResponse.of(ResultCode.NOTIFICATION_LIST_GET_SUCCESS, responseDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "해당 아이디의 알림을 읽음으로 수정"
    )
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ResultResponse> readNotification(
            HttpServletRequest request,
            @PathVariable("notificationId") Long notificationId
    ) {
        notificationService.readNotification(request, notificationId);
        ResultResponse response = ResultResponse.of(ResultCode.NOTIFICATION_READ_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "모든 알림 읽음 처리",
            description = "사용자의 읽지 않은 모든 알림을 읽음으로 수정"
    )
    @PatchMapping("/read")
    public ResponseEntity<ResultResponse> readAllUnreadNotification(
            HttpServletRequest request
    ) {
        notificationService.readAllUnreadNotification(request);
        ResultResponse response = ResultResponse.of(ResultCode.NOTIFICATION_ALL_READ_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "알림 삭제",
            description = "해당 아이디의 알림을 삭제"
    )
    @DeleteMapping("/{notificationId}/delete")
    public ResponseEntity<ResultResponse> deleteNotification(
            HttpServletRequest request,
            @PathVariable("notificationId") Long notificationId
    ) {
        notificationService.deleteNotification(request, notificationId);
        ResultResponse response = ResultResponse.of(ResultCode.NOTIFICATION_DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "모든 읽은 알림 삭제",
            description = "사용자의 모든 읽은 알림을 삭제"
    )
    @DeleteMapping("/unread/delete")
    public ResponseEntity<ResultResponse> deleteAllReadNotification(
            HttpServletRequest request
    ) {
        notificationService.deleteAllReadNotification(request);
        ResultResponse response = ResultResponse.of(ResultCode.NOTIFICATION_ALL_DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "읽지 않은 알림 수 조회",
            description = "사용자의 읽지 않은 모든 알림의 수 조회"
    )
    @GetMapping("/unread/count")
    public ResponseEntity<ResultResponse> countUnreadNotification(
            HttpServletRequest request
    ) {
        GetUnreadCountResponseDto responseDto = notificationService.getUnreadNotificationCount(request);
        ResultResponse response = ResultResponse.of(ResultCode.NOTIFICATION_UNREAD_COUNT_GET_SUCCESS, responseDto);
        return ResponseEntity.ok(response);
    }
}
