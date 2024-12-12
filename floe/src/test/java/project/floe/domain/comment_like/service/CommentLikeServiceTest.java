package project.floe.domain.comment_like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
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

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CommentLikeServiceTest {

    @Mock
    private CommentLikeJpaRepository commentLikeJpaRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentLikeService commentLikeService;

    private String email;
    private String email2;
    private Comment comment;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        email2 = "test2@example.com";
        comment = Comment.builder().id(1L).build();
        user = User.builder().id(1L).nickname("testUser").email(email).profileImage("image").build();
        user2 = User.builder().id(2L).nickname("testUser2").email(email2).profileImage("image2").build();
    }

    @DisplayName("댓글 좋아요 추가")
    @Test
    void createCommentLike() {
        Long commentId = 1L;

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(commentService.getCommentById(commentId)).thenReturn(comment);
        when(commentLikeJpaRepository.save(any(CommentLike.class))).thenReturn(new CommentLike(user, comment));

        commentLikeService.createCommentLike(mock(HttpServletRequest.class), commentId);

        verify(commentLikeJpaRepository, times(1)).save(any(CommentLike.class));
    }

    @DisplayName("중복된 댓글 좋아요 추가 시 예외 발생")
    @Test
    void createCommentLike_whenAlreadyLiked_throwsException() {
        Long commentId = 1L;

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(commentService.getCommentById(commentId)).thenReturn(comment);
        when(commentLikeJpaRepository.save(any(CommentLike.class)))
                .thenThrow(new BusinessException(ErrorCode.COMMENT_ALREADY_LIKED_ERROR));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentLikeService.createCommentLike(mock(HttpServletRequest.class), commentId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_ALREADY_LIKED_ERROR);

        verify(commentLikeJpaRepository, times(1)).save(any(CommentLike.class));
    }

    @DisplayName("댓글 좋아요 개수 조회")
    @Test
    void getCommentLikeCount() {
        Long commentId = 1L;

        when(commentService.getCommentById(commentId)).thenReturn(comment);
        when(commentLikeJpaRepository.countByCommentId(commentId)).thenReturn(5L);

        GetCommentLikeCountResponse response = commentLikeService.getCommentLikeCount(commentId);

        assertThat(response.count()).isEqualTo(5);
    }

    @DisplayName("댓글 좋아요 유저 목록 조회")
    @Test
    void getCommentLikeUsers() {
        Long commentId = 1L;
        List<CommentLike> commentLikes = List.of(new CommentLike(user, comment), new CommentLike(user2, comment));

        when(commentService.getCommentById(commentId)).thenReturn(comment);
        when(commentLikeJpaRepository.findUserListByCommentId(commentId)).thenReturn(List.of(
                new GetCommentLikeUserResponse(user),
                new GetCommentLikeUserResponse(user2))
        );

        GetCommentLikeUserListResponse response = commentLikeService.getCommentLikeUsers(commentId);

        assertThat(response.commentLikeUsers()).hasSize(2);
        assertThat(response.commentLikeUsers().get(0).getUserName()).isEqualTo("testUser");
        assertThat(response.commentLikeUsers().get(1).getUserName()).isEqualTo("testUser2");
    }

    @DisplayName("댓글 좋아요 삭제")
    @Test
    void deleteCommentLike() {
        Long commentId = 1L;

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(commentLikeJpaRepository.deleteByCommentIdAndUserId(commentId, user.getId())).thenReturn(1);

        commentLikeService.deleteCommentLike(mock(HttpServletRequest.class), commentId);

        verify(commentLikeJpaRepository, times(1)).deleteByCommentIdAndUserId(commentId, user.getId());
    }

    @DisplayName("댓글 좋아요 삭제 시 좋아요가 없으면 예외 발생")
    @Test
    void deleteCommentLike_whenLikeNotFound_throwsException() {

        Long commentId = 1L;

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(commentLikeJpaRepository.deleteByCommentIdAndUserId(commentId, user.getId())).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentLikeService.deleteCommentLike(mock(HttpServletRequest.class), commentId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_LIKE_NOT_FOUND_ERROR);
    }
}