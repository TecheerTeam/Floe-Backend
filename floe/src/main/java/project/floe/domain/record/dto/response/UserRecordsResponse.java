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
public class UserRecordsResponse {

    private Long recordId;

    private GetRecordUserResponse user;

    private String title;

    private String content;

    private RecordType recordType;

    private List<MediaResponse> medias;

    private List<String> tagNames;

    private LocalDateTime createdAt;

    public static UserRecordsResponse from(Record record){
        return UserRecordsResponse.builder()
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

    public static Page<UserRecordsResponse> listOf(Page<Record> records) {
        return records.map(UserRecordsResponse::from);
    }
}
