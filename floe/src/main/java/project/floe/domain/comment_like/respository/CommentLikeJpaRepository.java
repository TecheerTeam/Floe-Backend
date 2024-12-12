package project.floe.domain.comment_like.respository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.floe.domain.comment_like.dto.response.GetCommentLikeUserResponse;
import project.floe.domain.comment_like.entity.CommentLike;

@Repository
public interface CommentLikeJpaRepository extends JpaRepository<CommentLike, Long> {
    long countByCommentId(Long commentId);

    int deleteByCommentIdAndUserId(Long commentId, Long userId);

    @Query("SELECT new project.floe.domain.comment_like.dto.response.GetCommentLikeUserResponse(u.id, u.nickname, u.profileImage) "
            +
            "FROM CommentLike cl JOIN cl.users u WHERE cl.comment.id = :commentId")
    List<GetCommentLikeUserResponse> findUserListByCommentId(@Param("commentId") Long commentId);
}
