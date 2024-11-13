package project.floe.domain.record.controller;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.response.CreateRecordResponse;
import project.floe.domain.record.dto.response.GetDetailRecordResponse;
import project.floe.domain.record.dto.response.MediaResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
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
        Record newRecord = Record.builder()
                .userId(dto.getUserId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .recordType(dto.getRecordType())
                .build();
        CreateRecordResponse response = CreateRecordResponse.from(recordService.save(newRecord, files));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultResponse.of(ResultCode.RECORD_CREATE_SUCCESS, response));
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<ResultResponse> getDetailRecord(@PathVariable("recordId") Long recordId){
        Record findRecord = recordService.findRecordById(recordId);
        GetDetailRecordResponse response = GetDetailRecordResponse.builder()
                .userId(findRecord.getUserId())
                .title(findRecord.getTitle())
                .content(findRecord.getContent())
                .recordType(findRecord.getRecordType())
                .medias(findRecord.getMedias().stream()
                        .map(media -> MediaResponse.builder()
                                .mediaId(media.getId())
                                .mediaUrl(media.getMediaUrl())
                                .build()
                        ).collect(Collectors.toList())
                )
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResultResponse.of(ResultCode.DETAIL_RECORD_GET_SUCCESS, response));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<ResultResponse> deleteRecord(@PathVariable("recordId") Long recordId){
        recordService.deleteRecord(recordId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResultResponse.of(ResultCode.RECORD_DELETE_SUCCESS));
    }


}
