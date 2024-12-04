package project.floe.domain.record_save.dto.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import org.springframework.data.domain.Page;
import project.floe.domain.record.dto.response.GetRecordUserResponse;
import project.floe.domain.record.dto.response.MediaResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;

@Builder
public class GetSaveRecordsResponseDto {
    private Long recordId;

    private GetRecordUserResponse user;

    private String title;

    private String content;

    private RecordType recordType;

    private List<MediaResponse> medias;

    private List<String> tags;

    private LocalDateTime createdAt;

    public static GetSaveRecordsResponseDto from(Record record, List<FindMediasByRecordIdsResponseDto> mediaList) {

        List<MediaResponse> medias = mediaList.stream()
                .map(getMediasByRecordIdsResponseDto ->
                        MediaResponse.builder()
                                .mediaId(getMediasByRecordIdsResponseDto.getMediaId())
                                .mediaUrl(getMediasByRecordIdsResponseDto.getMediaUrl())
                                .build())
                .toList();


        return GetSaveRecordsResponseDto.builder()
                .recordId(record.getId())
                .user(GetRecordUserResponse.from(record.getUser()))
                .title(record.getTitle())
                .content(record.getContent())
                .recordType(record.getRecordType())
                .medias(medias)
                .tags(record.getTagNames())
                .createdAt(record.getCreatedAt())
                .build();
    }

    public static Page<GetSaveRecordsResponseDto> listOf(Page<Record> foundRecords,
                                                         List<FindMediasByRecordIdsResponseDto> responseDtoList) {

        Map<Long, List<FindMediasByRecordIdsResponseDto>> mediaMap =
                Optional.ofNullable(responseDtoList)
                        .orElse(Collections.emptyList())
                        .stream()
                        .collect(Collectors.groupingBy(FindMediasByRecordIdsResponseDto::getRecordId));


        return foundRecords.map(record -> {
            List<FindMediasByRecordIdsResponseDto> mediaList =
                    mediaMap.getOrDefault(record.getId(), Collections.emptyList());
            return GetSaveRecordsResponseDto.from(record, mediaList);
        });
    }
}
