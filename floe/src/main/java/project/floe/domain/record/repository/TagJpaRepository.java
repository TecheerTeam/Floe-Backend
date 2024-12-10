package project.floe.domain.record.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.floe.domain.record.entity.Tag;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t.tagName, COUNT(rt) FROM Tag t JOIN t.recordTags rt GROUP BY t.tagName")
    List<Object[]> findAllTagStatistics();

    @Query("SELECT COUNT(rt) FROM RecordTag rt")
    Long findTotalTagCount();
    public Optional<Tag> findByTagName(String tagName);
}
