package project.floe.domain.record.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;

@Repository
public interface RecordJpaRepository extends JpaRepository<Record, Long> {
    /*
     * 태그가 없을 때 검색 시에 제목과 레코드 타입을 이용하여 레코드를 찾는다.
     *
     * */
    Page<Record> findByTitleContainingAndRecordType(String title, RecordType recordType, Pageable pageable);

    Page<Record> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT r.id FROM Record r WHERE r.user.id = :userId")
    List<Long> findRecordIdsByUserId(@Param("userId") Long userId);
}
