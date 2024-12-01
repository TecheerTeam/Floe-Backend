package project.floe.domain.record_save.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.record_save.service.RecordSaveService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecordSaveController {

    private final RecordSaveService recordSaveService;

    @PostMapping("/records/{recordId}/save")
    public ResponseEntity<ResultResponse> addRecordSave(
            @PathVariable("recordId") Long recordId,
            HttpServletRequest request
    ) {
        recordSaveService.addRecordSave(recordId, request);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_SAVE_POST_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/records/{recordId}/save")
    public ResponseEntity<ResultResponse> deleteRecordSave(
            @PathVariable("recordId") Long recordId,
            HttpServletRequest request
    ) {
        recordSaveService.deleteRecordSave(recordId, request);
        ResultResponse response = ResultResponse.of(ResultCode.RECORD_SAVE_DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }
}
