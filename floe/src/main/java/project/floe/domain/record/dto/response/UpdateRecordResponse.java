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
public class UpdateRecordResponse {

    private Long recordId;

    private GetRecordUserResponse user;

    private String title;

    private String content;

    private RecordType recordType;

    private List<MediaResponse> medias;

    private List<String> tagNames;

    private LocalDateTime createdAt;

    public static UpdateRecordResponse from(Record record){
        return UpdateRecordResponse.builder()
                .recordId(record.getId())
                .user(GetRecordUserResponse.from(record.getUser()))
                .title(record.getTitle())
                .content(record.getContent())
                .recordType(record.getRecordType())
                .medias(MediaResponse.from(record))
                .tagNames(record.getTagNames())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
