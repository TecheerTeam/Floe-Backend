package project.floe.domain.comment.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.comment.dto.request.CreateCommentRequest;
import project.floe.domain.comment.dto.request.UpdateCommentRequest;
import project.floe.domain.comment.dto.response.GetCommentResponse;
import project.floe.domain.comment.entity.Comment;
import project.floe.domain.comment.repository.CommentJpaRepository;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.CommentException;
import project.floe.global.error.exception.EmptyResultException;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentJpaRepository commentRepository;
    private final JwtService jwtService;
    private final RecordJpaRepository recordRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createComment(CreateCommentRequest request, HttpServletRequest httpServletRequest) {

        Record record = recordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR));

        String userEmail = jwtService.extractEmail(httpServletRequest).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findByIdIncludingDeleted(request.getParentId())
                    .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_PARENT_NOT_FOUND_ERROR));
            if (parent.isDeleted()) {
                throw new CommentException(ErrorCode.COMMENT_CANNOT_REPLY_TO_DELETED);
            }
        }

        Comment comment = Comment.create(record, user, request.getContent(), parent);
        commentRepository.save(comment);
    }

    public Page<GetCommentResponse> getCommentsByRecordId(Long recordId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByRecordIdAndParentIsNull(recordId, pageable);
        return comments.map(GetCommentResponse::from);
    }

    public Page<GetCommentResponse> getParentComment(Long parentId, Pageable pageable) {
        Page<Comment> replies = commentRepository.findByParentId(parentId, pageable);
        return replies.map(GetCommentResponse::from);
    }

    @Transactional
    public void updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = getCommentById(commentId);

        if (comment.isDeleted()) {
            throw new CommentException(ErrorCode.COMMENT_DELETED_ERROR);
        }

        comment.updateContent(request.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getCommentById(commentId);

        if (comment.getParent() == null) {
            commentRepository.deleteAllByParentId(commentId);
        }

        commentRepository.delete(comment);

    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_ERROR));
    }


}