package project.floe.domain.comment.service;

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
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.CommentException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentJpaRepository commentRepository;
    /*
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    */

    @Transactional
    public void createComment(CreateCommentRequest request) {
    /*
    Record record = recordRepository.findById(request.getRecordId())
            .orElseThrow(() -> new CommentException(ErrorCode.RECORD_NOT_FOUND_ERROR));

    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_VALIDATION_ERROR));
    */

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findByIdIncludingDeleted(request.getParentId())
                    .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_PARENT_NOT_FOUND_ERROR));
            if (parent.isDeleted()) {
                throw new CommentException(ErrorCode.COMMENT_CANNOT_REPLY_TO_DELETED);
            }
        }

        Comment comment = Comment.create(request.getRecordId(), request.getUserId(), request.getContent(), parent);
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<GetCommentResponse> getCommentsByRecordId(Long recordId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByRecordId(recordId, pageable);
        return comments.map(GetCommentResponse::from);
    }

    @Transactional
    public void updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_ERROR));

        if (comment.isDeleted()) {
            throw new CommentException(ErrorCode.COMMENT_DELETED_ERROR);
        }

        comment.updateContent(request.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_ERROR));
        commentRepository.delete(comment);
    }


}