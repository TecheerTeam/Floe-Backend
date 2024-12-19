package project.floe.domain.notification.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

    private final Map<String,SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(String email, SseEmitter sseEmitter){
        emitters.put(email,sseEmitter);
        return sseEmitter;
    }

    public Optional<SseEmitter> findByEmail(String email){
        return Optional.ofNullable(emitters.get(email));
    }

    public void deleteByEmail(String email){
        emitters.remove(email);
    }
}
