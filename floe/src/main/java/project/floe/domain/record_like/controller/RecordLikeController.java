package project.floe.domain.record_like.controller;

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
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.dto.response.GetRecordLikeListResponseDto;
import project.floe.domain.record_like.service.RecordLikeService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "RecordLikeController", description = "기록 좋아요 API")
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordLikeController {

    private final RecordLikeService recordLikeService;

    @Operation(
            summary = "좋아요 수 조회",
            description = "좋아요 수 조회"
    )
    @GetMapping("/{recordId}/likes")
    public ResponseEntity<ResultResponse> getRecordLikeCount(
            @PathVariable("recordId") Long recordId
    ) {
        GetRecordLikeCountResponseDto dto = recordLikeService.getRecordLikeCount(recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_LIKE_COUNT_GET_SUCCESS, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "좋아요 추가",
            description = "좋아요 추가"
    )
    @PostMapping("/{recordId}/likes")
    public ResponseEntity<ResultResponse> addRecordLike(
        @PathVariable("recordId") Long recordId,
        HttpServletRequest request
    ){
        recordLikeService.addRecordLike(request,recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_LIKE_POST_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "좋아요 취소",
            description = "좋아요 취소"
    )
    @DeleteMapping("/{recordId}/likes")
    public ResponseEntity<ResultResponse> deleteRecordLike(
            @PathVariable("recordId") Long recordId,
            HttpServletRequest request
    ){
        recordLikeService.deleteRecordLike(request,recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_LIKE_DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "좋아요한 유저 목록",
            description = "좋아요한 유저 목록 조회"
    )
    @GetMapping("/{recordId}/like-list")
    public ResponseEntity<ResultResponse> getRecordLikeList(
            @PathVariable("recordId") Long recordId
    ){
        GetRecordLikeListResponseDto dto = recordLikeService.getRecordLikeList(recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_LIKE_LIST_GET_SUCCESS, dto);
        return ResponseEntity.ok(response);
    }
}
