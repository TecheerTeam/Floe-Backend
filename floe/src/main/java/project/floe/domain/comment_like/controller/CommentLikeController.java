package project.floe.domain.comment_like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.comment_like.dto.response.GetCommentLikeCountResponse;
import project.floe.domain.comment_like.dto.response.GetCommentLikeUserListResponse;
import project.floe.domain.comment_like.service.CommentLikeService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "CommentLikeController", description = "댓글 좋아요 API")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @Operation(
            summary = "댓글 좋아요 추가",
            description = "댓글 좋아요 추가"
    )
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<ResultResponse> addCommentLike(
            @PathVariable("commentId") Long commentId,
            HttpServletRequest httpServletRequest
    ) {
        commentLikeService.createCommentLike(httpServletRequest, commentId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultResponse.of(ResultCode.Comment_LIKE_CREATE_SUCCESS));
    }

    @Operation(
            summary = "댓글 좋아요 삭제",
            description = "댓글 좋아요 삭제"
    )
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<ResultResponse> deleteCommentLike(
            @PathVariable("commentId") Long commentId,
            HttpServletRequest httpServletRequest
    ) {
        commentLikeService.deleteCommentLike(httpServletRequest, commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.Comment_LIKE_DELETE_SUCCESS));
    }

    @Operation(
            summary = "댓글 좋아요 개수 조회",
            description = "댓글 좋아요 개수 조회"
    )
    @GetMapping("/{commentId}/likes/count")
    public ResponseEntity<ResultResponse> getCommentLikeCount(
            @PathVariable("commentId") Long commentId
    ) {
        GetCommentLikeCountResponse dto = commentLikeService.getCommentLikeCount(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.Comment_LIKE_COUNT_GET_SUCCESS, dto));
    }

    @Operation(
            summary = "댓글 좋아요 유저 목록 조회",
            description = "댓글 좋아요 유저 목록 조회"
    )
    @GetMapping("/{commentId}/likes/users")
    public ResponseEntity<ResultResponse> getCommentLikeUsers(
            @PathVariable("commentId") Long commentId
    ) {
        GetCommentLikeUserListResponse dto = commentLikeService.getCommentLikeUsers(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.Comment_LIKE_USERS_GET_SUCCESS, dto));
    }

}
