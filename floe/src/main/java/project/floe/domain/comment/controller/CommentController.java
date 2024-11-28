package project.floe.domain.comment.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.comment.dto.request.CreateCommentRequest;
import project.floe.domain.comment.dto.request.UpdateCommentRequest;
import project.floe.domain.comment.dto.response.GetCommentResponse;
import project.floe.domain.comment.service.CommentService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;
    
    @PostMapping
    public ResponseEntity<ResultResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {
        commentService.createComment(request); // 생성만 수행, 반환값 없음
        return ResponseEntity.status(HttpStatus.CREATED) // 201 Created 상태 반환
                .body(ResultResponse.of(ResultCode.COMMENT_CREATE_SUCCESS));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getComments(
            @RequestParam("recordId") Long recordId,
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<GetCommentResponse> response = commentService.getCommentsByRecordId(recordId, pageable);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.COMMENT_GET_SUCCESS, response));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ResultResponse> updateComment(@PathVariable(name = "commentId") Long commentId,
                                                        @Valid @RequestBody UpdateCommentRequest request) {
        commentService.updateComment(commentId, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.COMMENT_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResultResponse> deleteComment(@PathVariable(name = "commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.COMMENT_DELETE_SUCCESS));
    }
}