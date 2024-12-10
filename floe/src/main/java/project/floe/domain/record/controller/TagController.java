package project.floe.domain.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.record.dto.response.TagStatisticsResponse;
import project.floe.domain.record.service.TagStatisticsService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "TagController", description = "태그 통계 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagStatisticsService tagStatisticsService;

    @Operation(
            summary = "전체 태그 통계 조회",
            description = "사용되고 있는 태그들의 통계를 반환합니다."
    )
    @GetMapping("/all")
    public ResponseEntity<ResultResponse> getTagStatistics(){

          List<TagStatisticsResponse> tagStatistics = tagStatisticsService.getTagStatistics();

          return ResponseEntity.status(HttpStatus.OK)
                  .body(ResultResponse.of(ResultCode.GET_TAG_STATISTICS_SUCCESS,tagStatistics));
    }

    @Operation(
            summary = "사용자 태그 통계 조회",
            description = "사용자가 사용한 태그들의 통계를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<ResultResponse> getDetailTagStatistics(HttpServletRequest request){

        List<TagStatisticsResponse> userTagStatistics = tagStatisticsService.getUserTagStatistics(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResultResponse.of(ResultCode.GET_USER_TAG_STATISTICS_SUCCESS, userTagStatistics));
    }
}
