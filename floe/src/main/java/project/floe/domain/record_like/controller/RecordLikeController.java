package project.floe.domain.record_like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.record_like.dto.response.GetRecordLikeCountResponseDto;
import project.floe.domain.record_like.service.RecordLikeService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;


@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordLikeController {

    private final RecordLikeService recordLikeService;

    @GetMapping("/{recordId}/likes")
    public ResponseEntity<ResultResponse> getRecordLikeCount(
            @PathVariable("recordId") Long recordId
    ) {
        GetRecordLikeCountResponseDto dto = recordLikeService.getRecordLikeCount(recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_LIKE_COUNT_GET_SUCCESS, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{recordId}/likes")
    public ResponseEntity<ResultResponse> addRecordLike(
        @PathVariable("recordId") Long recordId
    ){
        // 로그인 구현 후 유저 정보 받기
        Long userId = 1L;
        recordLikeService.addRecordLike(userId,recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_LIKE_POST_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{recordId}/likes")
    public ResponseEntity<ResultResponse> deleteRecordLike(
            @PathVariable("recordId") Long recordId
    ){
        // 로그인 구현 후 유저 정보 받기
        Long userId = 1L;
        recordLikeService.deleteRecordLike(userId,recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_LIKE_DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }
}
