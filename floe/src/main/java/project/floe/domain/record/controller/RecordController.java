package project.floe.domain.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.request.SearchRecordRequest;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.CreateRecordResponse;
import project.floe.domain.record.dto.response.GetDetailRecordResponse;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.dto.response.UpdateRecordResponse;
import project.floe.domain.record.dto.response.UserRecordsResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.service.RecordService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "RecordController", description = "기록 API")
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/v1/records")
public class RecordController {

    private final RecordService recordService;

    @Operation(
            summary = "기록 생성",
            description = "트러블 슈팅 기록 생성"
    )
    @PostMapping
    public ResponseEntity<ResultResponse> createRecord(
            HttpServletRequest request,
            @Validated @RequestPart(value = "dto") CreateRecordRequest dto,
            @RequestPart(value = "files") List<MultipartFile> files) {

        CreateRecordResponse response = CreateRecordResponse.from(recordService.createRecord(request, dto, files));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultResponse.of(ResultCode.RECORD_CREATE_SUCCESS, response));
    }

    @Operation(
            summary = "기록 조회",
            description = "전체 기록 페이지네이션 조회"
    )
    @GetMapping
    public ResponseEntity<ResultResponse> getRecords(
            @PageableDefault(page = 0, size = 5, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
        Page<GetRecordResponse> response = recordService.findRecords(pageable);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RECORD_PAGING_GET_SUCCESS, response));
    }

    @Operation(
            summary = "기록 개별 조회",
            description = "기록 세부 페이지 조회"
    )
    @GetMapping("/{recordId}")
    public ResponseEntity<ResultResponse> getDetailRecord(@PathVariable("recordId") Long recordId) {
        Record findRecord = recordService.findRecordById(recordId);
        GetDetailRecordResponse response = GetDetailRecordResponse.from(findRecord);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResultResponse.of(ResultCode.DETAIL_RECORD_GET_SUCCESS, response));
    }

    @Operation(
            summary = "기록 수정",
            description = "트러블 슈팅 기록 수정"
    )
    @PutMapping("/{recordId}")
    public ResponseEntity<ResultResponse> updateRecord(@PathVariable("recordId") Long recordId,
                                                       @Validated @RequestPart("updateDto") UpdateRecordRequest updateDto,
                                                       @RequestPart("updateFiles") List<MultipartFile> updateFiles) {
        Record modifiedRecord = recordService.modifyRecord(recordId, updateDto, updateFiles);
        UpdateRecordResponse response = UpdateRecordResponse.from(modifiedRecord);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResultResponse.of(ResultCode.RECORD_MODIFY_SUCCESS, response));
    }

    @Operation(
            summary = "기록 삭제",
            description = "트러블 슈팅 기록 삭제"
    )
    @DeleteMapping("/{recordId}")
    public ResponseEntity<ResultResponse> deleteRecord(@PathVariable("recordId") Long recordId) {
        recordService.deleteRecord(recordId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResultResponse.of(ResultCode.RECORD_DELETE_SUCCESS));
    }

    @GetMapping("/search")
    public ResponseEntity<ResultResponse> searchRecord(
            @PageableDefault(page = 0, size = 5, sort = "updatedAt", direction = Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String title, // 제목
            @RequestParam(required = false) RecordType recordType, // Enum 값
            @RequestParam(required = false) List<String> tagNames // 태그 리스트
    ) {
        // SearchRecordRequest DTO 생성
        SearchRecordRequest dto = SearchRecordRequest.builder()
                .title(title)
                .recordType(recordType)
                .tagNames(tagNames)
                .build();

        // 서비스 호출
        Page<GetRecordResponse> response = recordService.searchRecords(pageable, dto);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RECORD_SEARCH_SUCCESS, response));
    }

    @Operation(
            summary = "회원 게시물 조회",
            description = "회원이 작성한 게시물 조회"
    )
    @GetMapping("/users")
    public ResponseEntity<ResultResponse> getUserRecords(
            HttpServletRequest request,
            @PageableDefault(page = 0, size = 5, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
        Page<UserRecordsResponse> userRecords = recordService.getUserRecords(request, pageable);
        return ResponseEntity.ok().body(ResultResponse.of(ResultCode.GET_USER_RECORDS_SUCCESS, userRecords));
    }

    @Operation(
            summary = "해당 유저 기록 조회",
            description = "해당 유저 기록 페이지네이션 조회"
    )
    @GetMapping("users/{userId}")
    public ResponseEntity<ResultResponse> getOtherUserRecords(
            @PathVariable("userId") Long userId,
            @PageableDefault(page = 0, size = 5, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
        Page<GetRecordResponse> response = recordService.findOtherUserRecords(userId,pageable);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RECORD_PAGING_GET_SUCCESS, response));
    }
}
