package project.floe.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.comment.dto.request.CreateCommentRequest;
import project.floe.domain.comment.dto.request.UpdateCommentRequest;
import project.floe.domain.comment.dto.response.GetCommentResponse;
import project.floe.domain.comment.service.CommentService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "CommentController", description = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "댓글 작성",
            description = "댓글 작성"
    )
    @PostMapping
    public ResponseEntity<ResultResponse> createComment(@Valid @RequestBody CreateCommentRequest request,
                                                        HttpServletRequest httpServletRequest) {
        commentService.createComment(request, httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultResponse.of(ResultCode.COMMENT_CREATE_SUCCESS));
    }

    @Operation(
            summary = "댓글 조회",
            description = "전체 댓글 조회"
    )
    @GetMapping("/{recordId}")
    public ResponseEntity<ResultResponse> getComments(
            @PathVariable("recordId") Long recordId,
            @PageableDefault(page = 0, size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<GetCommentResponse> response = commentService.getCommentsByRecordId(recordId, pageable);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.COMMENT_GET_SUCCESS, response));
    }

    @Operation(
            summary = "대댓글  조회 ",
            description = "대댓글 전체 조회'"
    )
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<ResultResponse> getParentComment(
            @PathVariable(name = "commentId") Long commentId,
            @PageableDefault(page = 0, size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<GetCommentResponse> response = commentService.getParentComment(commentId, pageable);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.COMMENT_GET_SUCCESS, response));
    }


    @Operation(
            summary = "댓글 수정 ",
            description = "댓글 수정 "
    )
    @PutMapping("/{commentId}")
    public ResponseEntity<ResultResponse> updateComment(@PathVariable(name = "commentId") Long commentId,
                                                        @Valid @RequestBody UpdateCommentRequest request) {
        commentService.updateComment(commentId, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.COMMENT_UPDATE_SUCCESS));
    }

    @Operation(
            summary = "댓글 삭제",
            description = "댓글 삭제'"
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResultResponse> deleteComment(@PathVariable(name = "commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.COMMENT_DELETE_SUCCESS));
    }
}