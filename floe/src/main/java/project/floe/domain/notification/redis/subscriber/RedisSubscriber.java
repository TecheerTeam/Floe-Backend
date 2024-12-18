package project.floe.domain.notification.redis.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.service.SseEmitterService;
import project.floe.global.error.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseEmitterService sseEmitterService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String receiverEmail = new String(message.getChannel());

            NotificationDto notificationDto = objectMapper.readValue(message.getBody(),
                    NotificationDto.class);

            sseEmitterService.sendNotificationToClient(receiverEmail, notificationDto);
        } catch (IOException e) {
            log.warn(ErrorCode.REDIS_MESSAGE_DESERIALIZATION_FAILED.getMessage());
        }
    }
}
