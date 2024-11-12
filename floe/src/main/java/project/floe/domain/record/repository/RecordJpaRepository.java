package project.floe.domain.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.floe.domain.record.entity.Record;

@Repository
public interface RecordJpaRepository extends JpaRepository<Record, Long> {
}
