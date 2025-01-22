package project.floe.domain.record_like.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import project.floe.domain.record.dto.response.GetRecordUserResponse;
import project.floe.domain.record.dto.response.MediaResponse;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetLikedRecordListDto {
    private Long recordId;

    private GetRecordUserResponse user;

    private String title;

    private String content;

    private RecordType recordType;

    private List<MediaResponse> medias;

    private List<String> tags;

    private LocalDateTime createdAt;

    public static GetLikedRecordListDto from(Record record) {
        return GetLikedRecordListDto.builder()
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
    public static Page<GetLikedRecordListDto> listOf(Page<Record> records) {
        return records.map(GetLikedRecordListDto::from);
    }
}
