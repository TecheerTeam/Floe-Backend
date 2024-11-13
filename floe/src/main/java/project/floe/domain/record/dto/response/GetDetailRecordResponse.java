package project.floe.domain.record.dto.response;

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
public class GetDetailRecordResponse {

    private Long userId;

    private String title;

    private String content;

    private RecordType recordType;

    private List<MediaResponse> medias;


}
