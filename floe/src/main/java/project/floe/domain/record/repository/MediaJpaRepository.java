package project.floe.domain.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.floe.domain.record.entity.Media;

@Repository
public interface MediaJpaRepository extends JpaRepository<Media,Long> {
}
