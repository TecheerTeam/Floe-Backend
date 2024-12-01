package project.floe.domain.record_save.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.floe.domain.record_save.entity.RecordSave;

@Repository
public interface RecordSaveRepository extends JpaRepository<RecordSave,Long> {
    Optional<RecordSave> findByUserIdAndRecordId(Long userId,Long RecordId);
}
