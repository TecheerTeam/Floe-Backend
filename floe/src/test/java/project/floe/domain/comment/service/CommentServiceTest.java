package project.floe.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import project.floe.domain.comment.dto.request.CreateCommentRequest;
import project.floe.domain.comment.dto.request.UpdateCommentRequest;
import project.floe.domain.comment.dto.response.GetCommentResponse;
import project.floe.domain.comment.entity.Comment;
import project.floe.domain.comment.repository.CommentJpaRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.CommentException;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    /*
    @MockBean
    private RecordRepository recordRepository;

    @MockBean
    private UserRepository userRepository;
    */
    @MockBean
    private CommentJpaRepository commentRepository;

    private CreateCommentRequest createCommentRequest;
    private UpdateCommentRequest updateCommentRequest;

    @BeforeEach
    void setUp() {
        createCommentRequest = CreateCommentRequest.builder()
                .recordId(1L)
                .userId(1L)
                .content("테스트 댓글")
                .parentId(null)
                .build();

        updateCommentRequest = UpdateCommentRequest.builder()
                .content("수정된 댓글 내용")
                .build();
    }

    @Test
    @DisplayName("부모 댓글이 없는 상태에서 댓글 생성 성공")
    void 댓글생성_부모댓글없음_성공() {
        Comment newComment = Comment.create(
                createCommentRequest.getRecordId(),
                createCommentRequest.getUserId(),
                createCommentRequest.getContent(),
                null
        );

        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        commentService.createComment(createCommentRequest);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("부모 댓글이 삭제된 상태에서 댓글 생성 시 예외 발생")
    void 댓글생성_부모댓글삭제됨_예외처리() {
        Comment parentComment = Comment.builder()
                .id(1L)
                .build();
        parentComment.delete();

        createCommentRequest = CreateCommentRequest.builder()
                .recordId(1L)
                .userId(1L)
                .content("테스트 댓글")
                .parentId(1L)
                .build();

        when(commentRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(parentComment));

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.createComment(createCommentRequest));

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

        when(commentRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.createComment(createCommentRequest));

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

        when(commentRepository.findById(1L)).thenReturn(Optional.of(deletedComment));

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.updateComment(1L, updateCommentRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_DELETED_ERROR);
    }

    @Test
    @DisplayName("댓글 수정 시 존재하지 않는 댓글 예외 발생")
    void 댓글수정_존재하지않는댓글() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.updateComment(1L, updateCommentRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND_ERROR);
    }

    @Test
    @DisplayName("댓글 삭제 시 존재하지 않는 댓글 예외 발생")
    void 댓글삭제_존재하지않는댓글() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

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

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).delete(comment);
    }


    @Test
    @DisplayName("댓글 수정 요청 내용 검증")
    void 댓글수정_성공() {
        Comment comment = Comment.builder()
                .id(1L)
                .content("기존 내용")
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.updateComment(1L, updateCommentRequest);

        assertThat(comment.getContent()).isEqualTo(updateCommentRequest.getContent());
    }


    @Test
    @DisplayName("댓글 페이징 조회 성공")
    void 댓글페이징조회_성공() {
        // Given: Mock 데이터 준비
        Comment comment1 = Comment.builder()
                .id(1L)
                .recordId(1L)
                .userId(1L)
                .content("댓글 내용 1")
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .recordId(1L)
                .userId(2L)
                .content("댓글 내용 2")
                .build();

        List<Comment> commentList = List.of(comment1, comment2);
        Page<Comment> mockPage = new PageImpl<>(commentList);

        Pageable pageable = PageRequest.of(0, 5); // 페이지 요청

        when(commentRepository.findByRecordId(1L, pageable)).thenReturn(mockPage);

        // When: 서비스 호출
        Page<GetCommentResponse> result = commentService.getCommentsByRecordId(1L, pageable);

        // Then: 검증
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2); // 총 댓글 수
        assertThat(result.getContent()).hasSize(2); // 페이지 내 댓글 수
        assertThat(result.getContent().get(0).getContent()).isEqualTo("댓글 내용 1");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("댓글 내용 2");
    }

    @Test
    @DisplayName("댓글 페이징 조회 - 빈 결과")
    void 댓글페이징조회_빈결과() {
        // Given: Mock 데이터 준비
        Page<Comment> mockPage = new PageImpl<>(List.of()); // 빈 페이지
        Pageable pageable = PageRequest.of(0, 5); // 페이지 요청

        when(commentRepository.findByRecordId(999L, pageable)).thenReturn(mockPage);

        // When: 서비스 호출
        Page<GetCommentResponse> result = commentService.getCommentsByRecordId(999L, pageable);

        // Then: 검증
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0); // 총 댓글 수가 0
        assertThat(result.getContent()).isEmpty(); // 페이지 내 댓글 없음
    }
}