package project.floe.domain.record_like.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.record_like.entity.RecordLike;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {

    long countByRecordId(Long recordId);

    List<RecordLike> findByRecordId(Long recordId);

    Optional<RecordLike> findByUserIdAndRecordId(Long userId, Long recordId);
}
