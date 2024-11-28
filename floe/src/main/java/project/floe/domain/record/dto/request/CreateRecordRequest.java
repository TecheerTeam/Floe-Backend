package project.floe.domain.record.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.entity.Tags;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateRecordRequest {

    private Long userId;

    @NotBlank(message = "Record title cannot be blank")
    private String title;

    @NotBlank(message = "Record content cannot be blank")
    private String content;

    @NotNull(message = "RecordType cannot be empty")
    private RecordType recordType;

    private List<String> tagNames;

    public Record toEntity(){
        return Record.builder()
                .userId(this.userId)
                .title(this.title)
                .content(this.content)
                .recordType(this.recordType)
                .recordTags(new RecordTags())
                .build();
    }
}
