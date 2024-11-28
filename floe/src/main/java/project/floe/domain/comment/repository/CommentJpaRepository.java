package project.floe.domain.comment.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.floe.domain.comment.entity.Comment;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByRecordId(Long recordId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.id = :parentId")
    Optional<Comment> findByIdIncludingDeleted(@Param("parentId") Long parentId);
}