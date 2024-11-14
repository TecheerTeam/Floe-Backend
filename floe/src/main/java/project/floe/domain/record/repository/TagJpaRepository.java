package project.floe.domain.record.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.floe.domain.record.entity.Tag;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    public Tag findTagByTagName(String tagName);
    public Optional<Tag> findByTagName(String tagName);
}
