package project.floe.domain.record.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.repository.RecordJpaRepository;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordJpaRepository repository;

    @Transactional
    public Long save(Record newRecord) {
        return repository.save(newRecord).getId();
    }
}
