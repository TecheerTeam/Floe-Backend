package project.floe.domain.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.floe.domain.media.entity.Media;

@Repository
public interface MediaJpaRepository extends JpaRepository<Media,Long> {
}
