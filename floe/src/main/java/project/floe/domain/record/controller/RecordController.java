package project.floe.domain.record.controller;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.request.UpdateRecordRequest;
import project.floe.domain.record.dto.response.CreateRecordResponse;
import project.floe.domain.record.dto.response.GetDetailRecordResponse;
import project.floe.domain.record.dto.response.GetRecordResponse;
import project.floe.domain.record.dto.response.UpdateRecordResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.EmptyResultException;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/v1/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<ResultResponse> createRecord(@Validated @RequestPart(value = "dto") CreateRecordRequest dto,
                                                       @RequestPart(value = "files") List<MultipartFile> files) {
        Record newRecord = dto.toEntity();
        CreateRecordResponse response = CreateRecordResponse.from(recordService.createRecord(newRecord, dto.getTagNames(), files));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultResponse.of(ResultCode.RECORD_CREATE_SUCCESS, response));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getRecords(@PageableDefault(page = 0, size = 5, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
        Page<GetRecordResponse> response = recordService.findRecords(pageable);
        if (response.isEmpty()) throw new EmptyResultException(ErrorCode.RECORD_NOT_FOUND_ERROR);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.Record_PAGING_GET_SUCCESS, response));
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<ResultResponse> getDetailRecord(@PathVariable("recordId") Long recordId) {
        Record findRecord = recordService.findRecordById(recordId);
        GetDetailRecordResponse response = GetDetailRecordResponse.from(findRecord);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResultResponse.of(ResultCode.DETAIL_RECORD_GET_SUCCESS, response));
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<ResultResponse> updateRecord(@PathVariable("recordId") Long recordId,
                                                       @Validated @RequestPart("updateDto") UpdateRecordRequest updateDto,
                                                       @RequestPart("updateFiles") List<MultipartFile> updateFiles) {
        Record modifiedRecord = recordService.modifyRecord(recordId, updateDto, updateFiles);
        UpdateRecordResponse response = UpdateRecordResponse.from(modifiedRecord);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResultResponse.of(ResultCode.RECORD_MODIFY_SUCCESS, response));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<ResultResponse> deleteRecord(@PathVariable("recordId") Long recordId) {
        recordService.deleteRecord(recordId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResultResponse.of(ResultCode.RECORD_DELETE_SUCCESS));
    }

}
