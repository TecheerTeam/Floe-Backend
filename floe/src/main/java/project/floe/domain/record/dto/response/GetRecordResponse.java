package project.floe.domain.record.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRecordResponse {

    private Long recordId;

    private GetRecordUserResponse user;

    private String title;

    private String content;

    private RecordType recordType;

    private List<MediaResponse> medias;

    private List<String> tags;

    private LocalDateTime createdAt;

    public static GetRecordResponse from(Record record){
        return GetRecordResponse.builder()
                .recordId(record.getId())
                .user(GetRecordUserResponse.from(record.getUser()))
                .title(record.getTitle())
                .content(record.getContent())
                .recordType(record.getRecordType())
                .medias(MediaResponse.from(record))
                .tags(record.getTagNames())
                .createdAt(record.getCreatedAt())
                .build();
    }

    public static Page<GetRecordResponse> listOf(Page<Record> records) {
        return records.map(GetRecordResponse::from);
    }

}
