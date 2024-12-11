package project.floe.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
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

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private RecordJpaRepository recordJpaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentJpaRepository commentJpaRepository;

    @Mock
    private JwtService jwtService;

    private CreateCommentRequest createCommentRequest;
    private UpdateCommentRequest updateCommentRequest;
    private Record record;
    private User user;
    private Comment parentComment;
    private String email;

    @BeforeEach
    void setUp() {

        email = "test@example.com";

        record = Record.builder()
                .id(1L)
                .build();

        user = User.builder()
                .id(1L)
                .nickname("testUser")
                .email("test@example.com")
                .build();

        createCommentRequest = CreateCommentRequest.builder()
                .recordId(1L)
                .userId(1L)
                .content("테스트 댓글")
                .parentId(null)
                .build();

        updateCommentRequest = UpdateCommentRequest.builder()
                .content("수정된 댓글 내용")
                .build();
        parentComment = Comment.builder()
                .id(1L)
                .record(record)
                .user(user)
                .build();
    }

    @Test
    @DisplayName("부모 댓글이 없는 상태에서 댓글 생성 성공")
    void 댓글생성_부모댓글없음_성공() {

        Comment newComment = Comment.create(
                record,
                user,
                createCommentRequest.getContent(),
                null
        );

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(recordJpaRepository.findById(record.getId())).thenReturn(Optional.of(record));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(commentJpaRepository.save(any(Comment.class))).thenReturn(newComment);

        commentService.createComment(createCommentRequest, mock(HttpServletRequest.class));

        verify(recordJpaRepository, times(1)).findById(record.getId());
        verify(userRepository, times(1)).findByEmail(email);
        verify(commentJpaRepository, times(1)).save(any(Comment.class));
        verify(jwtService, times(1)).extractEmail(any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("부모 댓글이 삭제된 상태에서 댓글 생성 시 예외 발생")
    void 댓글생성_부모댓글삭제됨_예외처리() {

        parentComment.delete();

        createCommentRequest = CreateCommentRequest.builder()
                .recordId(record.getId())
                .userId(user.getId())
                .content("테스트 댓글")
                .parentId(parentComment.getId())
                .build();

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(recordJpaRepository.findById(record.getId())).thenReturn(Optional.of(record));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(commentJpaRepository.findByIdIncludingDeleted(parentComment.getId())).thenReturn(
                Optional.of(parentComment));

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.createComment(createCommentRequest, mock(HttpServletRequest.class)));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_CANNOT_REPLY_TO_DELETED);
    }

    @Test
    @DisplayName("댓글 생성 시 존재하지 않는 부모 댓글 ID 예외 발생")
    void 댓글생성_존재하지않는부모댓글() {
        createCommentRequest = CreateCommentRequest.builder()
                .recordId(1L)
                .userId(1L)
                .content("테스트 댓글")
                .parentId(999L)
                .build();

        when(jwtService.extractEmail(any(HttpServletRequest.class))).thenReturn(Optional.of(email));
        when(recordJpaRepository.findById(record.getId())).thenReturn(Optional.of(record));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(commentJpaRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.createComment(createCommentRequest, mock(HttpServletRequest.class)));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_PARENT_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("삭제된 댓글 수정 시 예외 발생")
    void 댓글수정_삭제된댓글() {
        Comment deletedComment = Comment.builder()
                .id(1L)
                .content("기존 내용")
                .build();
        deletedComment.delete();

        when(commentJpaRepository.findById(1L)).thenReturn(Optional.of(deletedComment));

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.updateComment(1L, updateCommentRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_DELETED_ERROR);
    }

    @Test
    @DisplayName("댓글 수정 시 존재하지 않는 댓글 예외 발생")
    void 댓글수정_존재하지않는댓글() {
        when(commentJpaRepository.findById(1L)).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.updateComment(1L, updateCommentRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("댓글 삭제 시 존재하지 않는 댓글 예외 발생")
    void 댓글삭제_존재하지않는댓글() {
        when(commentJpaRepository.findById(1L)).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.deleteComment(1L));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void 댓글삭제_성공() {
        Comment comment = Comment.builder()
                .id(1L)
                .build();

        when(commentJpaRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentJpaRepository, times(1)).delete(comment);
    }


    @Test
    @DisplayName("댓글 수정 요청 내용 검증")
    void 댓글수정_성공() {
        Comment comment = Comment.builder()
                .id(1L)
                .content("기존 내용")
                .build();

        when(commentJpaRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.updateComment(1L, updateCommentRequest);

        assertThat(comment.getContent()).isEqualTo(updateCommentRequest.getContent());
    }


    @Test
    @DisplayName("댓글 페이징 조회 성공 테스트")
    void 댓글페이징조회_성공() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .record(record)
                .user(user)
                .content("댓글 내용 1")
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .record(record)
                .user(user)
                .content("댓글 내용 2")
                .build();

        List<Comment> comments = List.of(comment1, comment2);
        Page<Comment> commentPage = new PageImpl<>(comments);

        Pageable pageable = PageRequest.of(0, 5);

        when(commentJpaRepository.findByRecordId(1L, pageable)).thenReturn(commentPage);

        Page<GetCommentResponse> response = commentService.getCommentsByRecordId(1L, pageable);

        assertThat(response.getContent().size()).isEqualTo(2);
        assertThat(response.getContent().get(0).getContent()).isEqualTo("댓글 내용 1");
        assertThat(response.getContent().get(0).getUser().getNickname()).isEqualTo("testUser");
        assertThat(response.getContent().get(1).getContent()).isEqualTo("댓글 내용 2");
        assertThat(response.getContent().get(1).getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("댓글 페이징 조회 - 빈 결과")
    void 댓글페이징조회_빈결과() {

        Page<Comment> mockPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 5);

        when(commentJpaRepository.findByRecordId(999L, pageable)).thenReturn(mockPage);

        Page<GetCommentResponse> result = commentService.getCommentsByRecordId(999L, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }
}