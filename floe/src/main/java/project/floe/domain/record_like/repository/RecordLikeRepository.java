package project.floe.domain.record_like.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record_like.entity.RecordLike;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {

    long countByRecordId(Long recordId);

    List<RecordLike> findByRecordId(Long recordId);

    @EntityGraph(attributePaths = {"record","record.user"})
    @Query("SELECT rl.record FROM RecordLike rl "+
            "WHERE rl.user.id = :userId AND rl.record.isDeleted = false ")
    Page<Record> findByUserId(@Param("userId")Long userId, Pageable pageable);

    Optional<RecordLike> findByUserIdAndRecordId(Long userId, Long recordId);

}
