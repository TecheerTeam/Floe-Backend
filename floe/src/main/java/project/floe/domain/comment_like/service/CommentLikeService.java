package project.floe.domain.comment_like.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.comment.entity.Comment;
import project.floe.domain.comment.service.CommentService;
import project.floe.domain.comment_like.dto.response.GetCommentLikeCountResponse;
import project.floe.domain.comment_like.dto.response.GetCommentLikeUserListResponse;
import project.floe.domain.comment_like.dto.response.GetCommentLikeUserResponse;
import project.floe.domain.comment_like.entity.CommentLike;
import project.floe.domain.comment_like.respository.CommentLikeJpaRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.BusinessException;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeJpaRepository commentLikeJpaRepository;
    private final JwtService jwtService;
    private final CommentService commentservice;
    private final UserRepository userRepository;

    @Transactional
    public void createCommentLike(HttpServletRequest request, Long commentId) {
        Comment comment = commentservice.getCommentById(commentId);

        String email = jwtService.extractEmail(request)
                .orElseThrow(() -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));
        //동시성 문제로 인한 예외처리(DB에서 unique 제약조건으로 해결)
        try {
            commentLikeJpaRepository.save(new CommentLike(user, comment));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.COMMENT_ALREADY_LIKED_ERROR);
        }

    }

    @Transactional
    public void deleteCommentLike(HttpServletRequest request, Long commentId) {
        commentservice.getCommentById(commentId);

        String email = jwtService.extractEmail(request)
                .orElseThrow(() -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));
        //삭제된 행이 없을 경우 예외처리
        if (commentLikeJpaRepository.deleteByCommentIdAndUserId(commentId, user.getId()) == 0) {
            throw new BusinessException(ErrorCode.COMMENT_LIKE_NOT_FOUND_ERROR);
        }
    }

    public GetCommentLikeCountResponse getCommentLikeCount(Long commentId) {
        commentservice.getCommentById(commentId);
        long count = commentLikeJpaRepository.countByCommentId(commentId);
        return new GetCommentLikeCountResponse(count);
    }

    public GetCommentLikeUserListResponse getCommentLikeUsers(Long commentId) {
        commentservice.getCommentById(commentId);
        List<GetCommentLikeUserResponse> users = commentLikeJpaRepository.findUserListByCommentId(commentId);
        return new GetCommentLikeUserListResponse(users);
    }
}
