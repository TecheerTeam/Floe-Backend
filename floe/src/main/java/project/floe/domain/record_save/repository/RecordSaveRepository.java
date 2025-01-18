package project.floe.domain.record_save.repository;

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
import project.floe.domain.record_save.dto.response.FindMediasByRecordIdsResponseDto;
import project.floe.domain.record_save.entity.RecordSave;

@Repository
public interface RecordSaveRepository extends JpaRepository<RecordSave, Long> {

    Optional<RecordSave> findByUser_IdAndRecord_Id(Long userId, Long RecordId);

    Long countByRecord_Id(Long recordId);

    @EntityGraph(attributePaths = {"record", "record.user"})
    @Query("SELECT rs.record FROM RecordSave rs WHERE rs.user.id = :userId AND rs.record.isDeleted = false")
    Page<Record> findRecordsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new project.floe.domain.record_save.dto.response.FindMediasByRecordIdsResponseDto(m.record.id,m.id,m.mediaUrl) " +
            "FROM Media m WHERE m.record.id IN :recordIds")
    List<FindMediasByRecordIdsResponseDto> findMediasByRecordIds(@Param("recordIds") List<Long> recordIds);
}
