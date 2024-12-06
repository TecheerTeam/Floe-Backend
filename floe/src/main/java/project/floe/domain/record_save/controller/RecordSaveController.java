package project.floe.domain.record_save.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.record_save.dto.response.GetSaveCountResponseDto;
import project.floe.domain.record_save.dto.response.GetSaveRecordsResponseDto;
import project.floe.domain.record_save.service.RecordSaveService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "RecordSaveController", description = "기록 저장 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecordSaveController {

    private final RecordSaveService recordSaveService;

    @Operation(
            summary = "게시글 저장",
            description = "게시글 저장"
    )
    @PostMapping("/records/{recordId}/save")
    public ResponseEntity<ResultResponse> addRecordSave(
            @PathVariable("recordId") Long recordId,
            HttpServletRequest request
    ) {
        recordSaveService.addRecordSave(recordId, request);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_SAVE_POST_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "게시글 저장 취소",
            description = "게시글 저장 취소"
    )
    @DeleteMapping("/records/{recordId}/save")
    public ResponseEntity<ResultResponse> deleteRecordSave(
            @PathVariable("recordId") Long recordId,
            HttpServletRequest request
    ) {
        recordSaveService.deleteRecordSave(recordId, request);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_SAVE_DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "게시글 저장 횟수 조회",
            description = "게시글 저장 횟수 조회"
    )
    @GetMapping("/records/{recordId}/save-count")
    public ResponseEntity<ResultResponse> getSaveCount(
            @PathVariable("recordId") Long recordId
    ) {
        GetSaveCountResponseDto dto = recordSaveService.getSaveCountByRecordId(recordId);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_SAVE_COUNT_GET_SUCCESS, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "저장한 기록 목록 조회",
            description = "저장한 기록 목록 조회"
    )
    @GetMapping("/users/save/record-list")
    public ResponseEntity<ResultResponse> getSaveRecordList(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            HttpServletRequest request
    ) {
        Page<GetSaveRecordsResponseDto> dto = recordSaveService.getSaveRecordList(pageable, request);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_SAVE_LIST_GET_SUCCESS, dto);
        return ResponseEntity.ok(response);
    }

}
