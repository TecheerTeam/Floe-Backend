package project.floe.domain.notification.service;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.repository.SseEmitterRepository;
import project.floe.global.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final SseEmitterRepository sseEmitterRepository;

    @Value("${emitter.timeout}")
    private  Long EMITTER_TIMEOUT;

    public SseEmitter createEmitter(String email) {
        return sseEmitterRepository.save(email, new SseEmitter(EMITTER_TIMEOUT));
    }

    public void deleteEmitter(String email) {
        sseEmitterRepository.deleteByEmail(email);
    }

    public void send(Object data, String email, SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            sseEmitterRepository.deleteByEmail(email);
            log.warn(ErrorCode.SSE_EMITTER_SEND_FAILED.getMessage());
        }
    }

    public void sendNotificationToClient(String receiverEmail, NotificationDto notificationDto) {
        sseEmitterRepository.findByEmail(receiverEmail)
                .ifPresent(sseEmitter -> send(notificationDto, receiverEmail, sseEmitter));
    }
}
