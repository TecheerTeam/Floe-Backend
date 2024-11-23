package project.floe.domain.record_like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.record_like.entity.RecordLike;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {
    boolean existsByRecordId(Long recordId);

    long countByRecordId(Long recordId);

    @Modifying
    @Query(value = "INSERT INTO record_like(user_id, record_id) VALUES(:userId, :recordId)", nativeQuery = true)
    void addLike(@Param("userId") Long userId, @Param("recordId") Long recordId);

    @Modifying
    @Query(value = "DELETE FROM record_like WHERE user_id = :userId AND record_id = :recordId", nativeQuery = true)
    void deleteLike(@Param("userId") Long userId, @Param("recordId") Long recordId);

}
