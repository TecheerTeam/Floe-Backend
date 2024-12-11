package project.floe.domain.record.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.floe.domain.record.entity.Tag;

@Repository
public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    public Optional<Tag> findByTagName(String tagName);
}
