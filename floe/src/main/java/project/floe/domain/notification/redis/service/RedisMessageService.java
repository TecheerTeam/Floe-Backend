package project.floe.domain.notification.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.redis.subscriber.RedisSubscriber;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageService {

    private final RedisMessageListenerContainer container;
    private final RedisSubscriber subscriber;
    private final RedisTemplate<String, Object> redisTemplate;

    public void subscribe(String channel) {
        container.addMessageListener(subscriber, ChannelTopic.of(channel));
    }

    public void publish(String channel, NotificationDto notificationDto) {
        redisTemplate.convertAndSend(channel, notificationDto);
    }

    public void removeSubscribe(String channel) {
        container.removeMessageListener(subscriber, ChannelTopic.of(channel));
    }
}
