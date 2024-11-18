package project.floe.domain.record.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDetailRecordResponse {

    private Long recordId;

    private Long userId;

    private String title;

    private String content;

    private RecordType recordType;

    private List<MediaResponse> medias;

    private List<String> tags;

    private LocalDateTime createdAt;

    public static GetDetailRecordResponse from(Record record){
        return GetDetailRecordResponse.builder()
                .recordId(record.getId())
                .userId(record.getUserId())
                .title(record.getTitle())
                .content(record.getContent())
                .recordType(record.getRecordType())
                .medias(MediaResponse.from(record))
                .tags(record.getTagNames())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
