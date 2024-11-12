package project.floe.domain.record.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.media.entity.Media;
import project.floe.domain.media.service.MediaService;
import project.floe.domain.record.dto.request.CreateRecordRequest;
import project.floe.domain.record.dto.response.CreateRecordResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.service.RecordService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
public class RecordController {

    private final RecordService recordService;
    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<ResultResponse> createRecord(@RequestPart(value = "dto") CreateRecordRequest dto,
                                                       @RequestPart(value = "files") List<MultipartFile> files) {
        List<Media> medias = mediaService.uploadFiles(files);
        Record newRecord = Record.builder()
                .userId(dto.getUserId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .recordType(dto.getRecordType())
                .medias(medias)
                .build();
        CreateRecordResponse response = CreateRecordResponse.from(recordService.save(newRecord));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultResponse.of(ResultCode.RECORD_CREATE_SUCCESS, response));
    }
}
