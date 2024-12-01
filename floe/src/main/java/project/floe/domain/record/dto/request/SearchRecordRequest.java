package project.floe.domain.record.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.record.entity.RecordType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRecordRequest {

    private String title;

    private RecordType recordType;

    private List<String> tagNames;

}
