package project.floe.domain.comment.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.comment.entity.Comment;

@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByRecordIdAndParentIsNull(Long recordId, Pageable pageable);

    Page<Comment> findByParentId(Long parentId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.id = :parentId AND (c.isDeleted = false OR c.isDeleted = true)")
    Optional<Comment> findByIdIncludingDeleted(@Param("parentId") Long parentId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parent.id = :parentId")
    void deleteAllByParentId(@Param("parentId") Long parentId);
}