package project.floe.domain.notification.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.redis.subscriber.RedisSubscriber;
import project.floe.global.config.RedisConfig;

@ActiveProfiles("test")
@DataRedisTest
@Import(RedisConfig.class)
public class RedisPubSubTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisMessageListenerContainer container;

    @InjectMocks
    private RedisSubscriber redisSubscriber;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void 레디스채널송수신() throws InterruptedException, IOException {

        String receiverEmail = "receiverEmail";
        NotificationDto notificationDto = NotificationDto.builder()
                .id(1L)
                .receiverEmail(receiverEmail)
                .build();

        CountDownLatch latch = new CountDownLatch(1);
        doReturn(notificationDto).when(objectMapper).readValue(any(byte[].class), eq(NotificationDto.class));
        doAnswer(invocationOnMock -> {
            latch.countDown();
            return null;
        }).when(sseEmitterService).sendNotificationToClient(anyString(), any(NotificationDto.class));

        container.addMessageListener(redisSubscriber, new ChannelTopic(receiverEmail));
        redisTemplate.convertAndSend(receiverEmail, notificationDto);

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        verify(sseEmitterService, times(1)).sendNotificationToClient(anyString(), any(NotificationDto.class));
    }
}
