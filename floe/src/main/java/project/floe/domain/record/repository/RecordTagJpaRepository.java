package project.floe.domain.record.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTag;
import project.floe.domain.record.entity.RecordType;

@Repository
public interface RecordTagJpaRepository extends JpaRepository<RecordTag, Long> {
    /*
     *  검색 시에 태그와 제목, 레코드 타입을 이용하여 레코드를 찾는다.
     *  dto로 붙어 받은 tagNames와 같은 tag를 찾고 tag_id를 이용해 record_tag를 찾는다.
     *  record_tag.record_id를 이용해 record를 찾고 record.title과 record.recordType을 이용해 검색한다.
     */
    @Query("SELECT r FROM Record r " +
            "JOIN r.recordTags.value rt " +
            "JOIN rt.tag t " +
            "WHERE t.tagName IN :tagNames " +
            "AND r.title LIKE CONCAT('%', :title, '%') " +
            "AND r.recordType = :recordType")
    Page<Record> findRecordsByTagsAndTitleAndRecordType(
            @Param("tagNames") List<String> tagNames,
            @Param("title") String title,
            @Param("recordType") RecordType recordType,
            Pageable pageable);

    @Query("SELECT rt.tag.tagName, COUNT(rt) FROM RecordTag rt WHERE rt.record.id IN :recordIds GROUP BY rt.tag.id")
    List<Object[]> findTagStatisticsByRecordIds(@Param("recordIds") List<Long> recordIds);

    @Query("SELECT COUNT(rt) FROM RecordTag rt WHERE rt.record.id IN :recordIds")
    Long findTotalTagCountByRecordIds(@Param("recordIds") List<Long> recordIds);

}
